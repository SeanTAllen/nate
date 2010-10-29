require 'rubygems'
require 'nokogiri'
require 'lorax'

module Nate
  class Engine
    CONTENT_ATTRIBUTE = '*content*'
    
    def self.from_string source, encoder_type = :html
      self.new source, encoder_type
    end

    def self.from_file path
      case path
      when /\.htm(l)?/
        encoder_type = :html
      when /\.h(a)?ml/
        encoder_type = :haml
      else
        raise "Unsupported file type"
      end
      self.new File.new( path ).read, encoder_type
    end

    def initialize source, encoder_type = :html
      @template = source.sub( /<\?xml version="1.0"\?>\n?/, '')
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
      if is_document_fragment?( template )
        fragment = transform( Nokogiri::XML.fragment( template ), data )
      else
        fragment = transform( Nokogiri::XML.parse( template ), data )        
      end
      Nate::Engine.from_string fragment.to_xml
    end

    def select selector
      template = encode_template()
      if is_document_fragment?( template )
        selection = Nokogiri::XML.fragment( template ).css( selector.to_s ).to_xml
      else
        selection = Nokogiri::XML.parse( template).css( selector.to_s ).to_xml
      end
      Nate::Engine.from_string selection
    end
    
    def render
      encode_template()
    end

    alias :to_html :render
    alias :to_s :render
    
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
        node.css( selector.to_s).each do | subnode | 
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
      if is_document_fragment?( string )
        Nokogiri::XML.fragment( string )
      else
        Nokogiri::XML.parse( string )
      end
    end
    
    def is_document_fragment?( string )
      return true if string == ''
      Lorax::Signature.new( Nokogiri::HTML.parse( Nokogiri::HTML.fragment(string).to_xml ).root ).signature == Lorax::Signature.new( Nokogiri::HTML.parse(string).root).signature
    end
  end
end
