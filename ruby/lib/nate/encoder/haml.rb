require 'rubygems'
require 'haml'

module Nate
  class HamlEncoder
    def encode string
      haml = Haml::Engine.new string
      haml.to_html
    end
  end
end