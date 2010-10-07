Given /^the plain HTML fragment "([^"]*)"$/ do |html|
  @nate = Nate::Template.new html
end

When /^"([^"]*)" is injected$/ do | data|
  @transformed_html = @nate.inject_with data
end

Then /^the plain HTML fragment is "([^"]*)"$/ do |expected_html|
  expected_html.should == @transformed_html
end
