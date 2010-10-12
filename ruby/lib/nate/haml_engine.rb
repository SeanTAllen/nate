require 'nate/engine'

require 'rubygems'
require 'haml'

module Nate
  class HamlEngine < Engine
    private
    def encode_template
       haml = Haml::Engine.new @template
       haml.to_html
    end
  end
end