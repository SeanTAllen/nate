spec = Gem::Specification.new do |s|
  s.name = 'Nate'
  s.version = '0.1'
  s.summary = 'A HTML templating engine that uses logicless and valueless templates.'
  s.description <<-EOF
Description of Nate here.
  EOF
  s.authors = [ 'Sean T Allen', 'James Ladd' ]
  s.homepage = 'http://github.com/jamesladd/nate/tree/master/ruby/'
  s.add_dependency = 'nokogiri'
  s.add_development_dependency = 'cucumber'
  s.add_development_dependency = 'lorax'
end
