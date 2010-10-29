Before do
  @nate_states = []
end

Given /^the HTML fragment "([^"]*)"$/ do |html|
  @nate_states.push( Nate::Engine.from_string html, :html )
end

Given /^the file "([^"]*)"$/ do |file|
  @nate_states.push( Nate::Engine.from_file file )
end

When /(.*) is injected$/ do |data|
  nate = @nate_states.last
  @nate_states.push( nate.inject_with eval(data) )
end

When /^"([^"].*)" is selected$/ do |selector|
  nate = @nate_states.last
  @nate_states.push ( nate.select( selector ) )
end

And /(.*) is injected sometime later$/ do |data|
  When %{#{data} is injected}
end

Then /^the HTML fragment is (.*)$/ do |expected_html|
  #@nate_states.last.render.should == expected_html
  transformed_html = @nate_states.last.render
  Lorax::Signature.new( Nokogiri::HTML(transformed_html).root ).signature.should == Lorax::Signature.new( Nokogiri::HTML(expected_html).root ).signature
end

Then /^the original HTML fragment is (.*)$/ do |expected_html|
  transformed_html = @nate_states.first.render
  Lorax::Signature.new( Nokogiri::HTML(transformed_html).root ).signature.should == Lorax::Signature.new( Nokogiri::HTML(expected_html).root ).signature
end
