require 'nate/engine'

module Nate
  class HTMLEngine < Engine
    private
    def encode_template
      @template
    end
  end
end