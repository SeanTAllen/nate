require 'rubygems'
require 'hpricot'

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
      fragment = transform( Hpricot( encode_template() ), data )
      Nate::Engine.from_string fragment.to_html
    end

    def select selector
      selection = Hpricot( encode_template() ).search( selector.to_s ).inner_html
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
        values.each do | selector, value |
          node.search( selector.to_s).each do | subnode | 
            transform( subnode, value ) 
          end
        end
      else
        values.each do | attribute, value |
          unless attribute == CONTENT_ATTRIBUTE
            transform_attribute( node, attribute, value )
          else
            transform_node( node, value)
          end
        end
      end
    end

    def transform_list( node, values )
      nodes = values.collect do | value |
        node_copy = Hpricot( node.to_html ).root
        transform( node_copy, value )
        node_copy.to_html
      end
      node_html = nodes.empty? ? ' ' : nodes.join
      node.swap( node_html )
    end

    def transform_node( node, value )
      node.inner_html = value.to_s unless value.nil?
    end

    def transform_attribute( node, attribute, value )
      node.attributes[ attribute ] = value.to_s
    end

    def contains_attributes( node, values )
      values.keys.any? do | key | 
        begin
          node.has_attribute?( key )
        rescue
          false 
        end
      end
    end
  end
end
