Given /^the plain HTML fragment "([^"]*)"$/ do |html|
  @nate = Nate::Template.new html
end

When /^(.*) is injected$/ do | data|
  @transformed_html = @nate.inject_with eval(data)
end

Then /^the plain HTML fragment is (.*)$/ do |expected_html|
  Lorax::Signature.new( Nokogiri::HTML(@transformed_html).root ).signature.should == Lorax::Signature.new( Nokogiri::HTML(expected_html).root ).signature
end
