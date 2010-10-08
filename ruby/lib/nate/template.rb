require 'rubygems'
require 'nokogiri'

module Nate
  class Template
    def initialize html
      @html = html
    end
    
    def inject_with data
      nokogiri_fragment = transform Nokogiri::HTML.fragment( @html ), data
      nokogiri_fragment.to_html
    end
    
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
      values.each { | selector, value |
        node.css( selector.to_s).each { | subnode |
            transform( subnode, value )
          }
        }
    end
    
    def transform_list( node, values )
      nodes = []
      values.each { | value |
        node_copy = node.clone
        transform( node_copy, value )
        nodes << node_copy
      }
      node.replace( nodes.join )
    end
    
    def transform_node node, value
      node.content = value
    end
  end
end
