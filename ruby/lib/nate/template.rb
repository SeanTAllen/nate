require 'rubygems'
require 'nokogiri'

module Nate
  class Template
    def initialize html
      @html = html
    end
    
    def inject_with data
      parsed = Nokogiri::HTML.fragment( @html )
      data.each { | selector, value |
        parsed.css( selector ).each { | node |
          if ( value.kind_of?( Array ) )
            transform_list( node, value )
          else
            transform_node( node, value )
         end
        }
      }
      return parsed.to_html
    end
    
    private
    def transform_list node, values
      nodes = []
      values.each { | value |
        node_copy = node.clone
        transform_node( node_copy, value )
        nodes << node_copy
      }
      node.replace( nodes.join )
    end
    
    def transform_node node, value
      node.content = value
    end
  end
end
