require 'rubygems'
require 'nokogiri'

module Nate
  class Engine
    CONTENT_ATTRIBUTE = '*content*'
    
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
      template = encode_template()
      fragment = transform( Nokogiri::XML.fragment( template ), data )
      Nate::Engine.from_string fragment.to_xml
    end

    def select selector
      fragment = Nokogiri::XML.fragment( encode_template() )
      if selector =~ /^content:/
        selector.gsub! /^content:/, ''
        selection = select_all( fragment, selector )
      else
        selection = select_elements( fragment, selector )
      end
      Nate::Engine.from_string selection.to_xml
    end
    
    def render encode_as = :html
      template = encode_template()
      to_method = string_to_fragment( template ).method( "to_#{encode_as}")
      to_method.call
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
      unless contains_attributes( node, values)
        transform_subselection_hash( node, values )
      else
        transform_attribute_hash( node, values )
      end
    end

    def transform_subselection_hash( node, values )
      values.each do | selector, value |
        search( node, selector ).each do | subnode | 
          transform( subnode, value ) 
        end
      end
    end
    
    def transform_attribute_hash( node, values )
      values.each do | attribute, value |
        unless attribute == CONTENT_ATTRIBUTE
          transform_attribute( node, attribute, value )
        else
          transform_node( node, value)
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

    def transform_attribute( node, attribute, value )
      if has_attribute?( node, attribute )
        node[ attribute ] = value.to_s
      end
    end

    def contains_attributes( node, values )
      values.keys.any? { | key | has_attribute?( node, key ) }
    end
    
    def has_attribute?( node, attribute )
      node[ attribute ].nil? == false 
    end
    
    def string_to_fragment( string )
      Nokogiri::XML.fragment( string )
    end
    
    def search( fragment_or_node, selector )
      ns = namespace( fragment_or_node )
      args = [ selector.to_s ]
      args.push ns if has_namespace?( fragment_or_node )
      fragment_or_node.search( *args )
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
    
    def namespace( fragment )
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
  end
end

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
