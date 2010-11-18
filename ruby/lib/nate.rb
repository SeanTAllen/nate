require 'rubygems'
require 'nokogiri'

module Nate
  class Engine
    CONTENT_SELECTOR   = '##'
    ATTRIBUTE_SELECTOR = '@@'
    CONTENT_ATTRIBUTE  = "*content*"
    
    def self.from_string source, encoder_type = :html
      self.new source, encoder_type
    end

    def self.from_file path
      case path
      when /\.(x)?htm(l)?/
        encoder_type = :html
      when /\.h(a)?ml/
        encoder_type = :haml
      else
        raise "Unsupported file type"
      end
      self.new File.new( path ).read, encoder_type
    end

    def initialize source, encoder_type = :html
      @template = source
      case encoder_type
      when :html
        require 'nate/encoder/html'
      when :haml
        require 'nate/encoder/haml'
      else
        raise "Nate encoder type needs to be set"
      end
    end

    def inject_with data
      fragment = transform( template_to_fragment(), data )
      Nate::Engine.from_string fragment.to_xml
    end

    def select selector
      fragment = template_to_fragment()
      if selector =~ /^#{CONTENT_SELECTOR}/
        selector.gsub! /^#{CONTENT_SELECTOR}/, ''
        selection = select_all( fragment, selector )
      else
        selection = select_elements( fragment, selector )
      end
      Nate::Engine.from_string selection.to_xml
    end
    
    def render render_as = :html
      doc_fragment = template_to_fragment()
      render_method = doc_fragment.method( "to_#{render_as}")
      render_method.call
    end

    def to_html
      render :html
    end
    
    def to_xhtml
      render :xhtml
    end
    
    def to_xml
      render :xml
    end
    
    alias :to_s :to_xml
    
    private    
    def transform( node, values )
      if ( values.kind_of?( Hash ) )
        transform_hash( node, values )
      elsif ( values.kind_of?( Array ) )
        transform_list( node, values )
      else
        transform_node( node, values)
      end
      return node
    end

    def transform_hash( node, values)
      values.each do | selector, value |        
        unless selector_contains_attributes?( selector )
          search( node, selector ).each do | subnode | 
            transform( subnode, value )
          end
        else
          if selector_is_for_attribute_only?( selector )
            selectors = split_selector_on_attributes( selector )
            transform_attribute( node, selectors, value )
          else
            selectors = split_selector_on_attributes( selector )
            search( node, selectors.shift ).each do | subnode |
              transform_attribute( subnode, selectors, value )
            end
          end
        end
      end
    end
    
    def transform_list( node, values )
      nodes = []
      values.each do | value |
        node_copy = node.clone
        transform( node_copy, value )
        nodes << node_copy
      end
      node.replace( string_to_fragment( nodes.join ) )
    end

    def transform_node( node, value )
      node.inner_html = string_to_fragment( value.to_s ) unless value.nil?
    end

    def transform_attribute( node, possible_attributes, value )
      attribute = possible_attributes.detect { | item | item != "" }
      unless attribute == CONTENT_ATTRIBUTE
        node[ attribute ] = value.to_s
      else
        transform( node, value )
      end
    end

    def search( fragment_or_node, selector )
      ns = namespace_for( fragment_or_node )
      args = [ selector.to_s ]
      args.push ns if has_namespace?( fragment_or_node )
      fragment_or_node.search( *args )        
    end
    
    def selector_is_for_attribute_only?( selector )
      selectors = split_selector_on_attributes( selector)
      if ((selectors.length > 1) && (selectors[0] == "" )) || selectors[ 0 ] == CONTENT_ATTRIBUTE
        true
      else
        false
      end
    end
    
    def selector_contains_attributes?( selector )
      split_selector_on_attributes( selector).length > 1 || selector == CONTENT_ATTRIBUTE
    end
    
    def split_selector_on_attributes( selector )
      selector.split /\s*#{ATTRIBUTE_SELECTOR}/  
    end
    
    def select_elements( fragment, selector )
      selection = search( fragment, selector )
    end
    
    def select_all( fragment, selector )
      all = select_elements( fragment, selector ).inner_html
      string_to_fragment( all )
    end
    
    def has_namespace? fragment
      fragment.children().each() do | node |
        begin
          return true if node.namespace.href
        rescue
          ''
        end
      end   
      return false   
    end
    
    def namespace_for( fragment )
      fragment.children().each() do | node |
        begin
          if node.namespace.href
            ns = node.namespace.prefix ? "xmlns:#{node.namespace.prefix}" : 'xmlns'
            return { ns => node.namespace.href}
          end
        rescue
          ''
        end
      end
    end
    
    def string_to_fragment( string )
      Nokogiri::XML.fragment( string )
    end
    
    def template_to_fragment
      string_to_fragment( encode_template() )
    end 
  end
end

# work around for missing nokogiri feature ( search working correctly on document fragments )
# and bug fix that said feature exposed. these will go into nokogiri eventually but first,
# xpath issues have to be addressed. this monkeypatch will break xpath support using 'search'
# on document fragments w/ nokogiri but, those dont function properly in most cases right now
# anyway.

module Nokogiri
  module XML
    class DocumentFragment
      def search( *args )
        if children.any?
          children.search(*args)
        else
          NodeSet.new(document)
        end
      end
    end
    
    class NodeSet
      def css *paths
        handler = ![
          Hash, String, Symbol
        ].include?(paths.last.class) ? paths.pop : nil

        ns = paths.last.is_a?(Hash) ? paths.pop : nil

        sub_set = NodeSet.new(document)

        each do |node|
          doc = node.document
          search_ns = ns || (doc.root ? doc.root.namespaces : {})

          xpaths = paths.map { |rule|
            [
              CSS.xpath_for(rule.to_s, :prefix => ".//", :ns => search_ns),
              CSS.xpath_for(rule.to_s, :prefix => "self::", :ns => search_ns)
            ].join(' | ')
          }

          sub_set += node.xpath(*(xpaths + [search_ns, handler].compact))
        end
        document.decorate(sub_set)
        sub_set
      end
    end
  end
end
