require 'nate/template'

module Nate
  class HTMLTemplate < Template
    private
    def template_to_html
      @html
    end
  end
end