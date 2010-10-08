require 'nate/template'

require 'rubygems'
require 'haml'

module Nate
  class HamlTemplate < Template
    private
    def template_to_html
       haml = Haml::Engine.new @html
       haml.to_html
    end
  end
end