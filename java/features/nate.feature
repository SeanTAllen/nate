Feature:

 As someone working on a dynamic website
 In order to keep the HTML free from logic
 I want 'nate' to transform HTML based on data I supply

  Scenario: empty data results in no change to the html
    Given the HTML fragment "<ul><li></li></ul>"
      When {} is injected
      Then the HTML fragment is <ul><li></li></ul>
 
  Scenario: nil value shouldn't modify matched element
    Given the HTML fragment "<h1>Header</h1>"
      When { 'h1' => nil } is injected
      Then the HTML fragment is <h1>Header</h1>
  
  Scenario: unmatched selectors result in no change to the html
    Given the HTML fragment "<h1>Header</h1>"
      When { 'h2' => 'Monkey' } is injected
      Then the HTML fragment is <h1>Header</h1>
            
  Scenario Outline: match and inject a single data value
    Given the HTML fragment "<div class='section'><span class='content'></span></div>"
     When <data> is injected
     Then the HTML fragment is <transformed>
    
    Examples:
      | data                             | transformed                                                           |
      | {'.section' => 'Hello Section' } | <div class="section">Hello Section</div>                              |
      | {'.content' => 'Hello Content'}  | <div class="section"><span class="content">Hello Content</span></div> |

  Scenario Outline: match and inject multiple data values 
    Given the HTML fragment "<div class='section'><span class='content'></span></div>"
      When <data> is injected
      Then the HTML fragment is <transformed>
      
    Examples:
      | data                                          | transformed                                             |
      | {'.section' => [ 'Section 1', 'Section 2' ] } | <div class="section">Section 1</div><div class="section">Section 2</div>                                |
      | {'.content' => [ 'Content 1', 'Content 2' ]}  | <div class="section"><span class="content">Content 1</span><span class="content">Content 2</span></div> |

  Scenario Outline: match and inject values into a subselection of matched html
    Given the HTML fragment "<div class='section'><span class='greeting'></span></div>"
      When <data> is injected
      Then the HTML fragment is <transformed>
    
    Examples:
      | data                                         | transformed |
      | { '.section' => { '.greeting' => 'Hello' } } | <div class="section"><span class="greeting">Hello</span></div> |
      | { '.section' => { 'span' => 'Hello' } }      | <div class="section"><span class="greeting">Hello</span></div> |
  
  Scenario Outline: match and inject multiple data values into a subselection of matched html
    Given the HTML fragment "<div class='section'><span class='greeting'></span></div>"
      When <data> is injected
      Then the HTML fragment is <transformed>
      
    Examples:
      | data                                                                           | transformed |
      | { '.section' => [ { '.greeting' => 'Hello' }, { '.greeting' => 'Goodbye' } ] } | <div class="section"><span class="greeting">Hello</span></div><div class="section"><span class="greeting">Goodbye</span></div> |
      | { '.section' => [ { 'span' => 'Hello' }, { 'span' => 'Goodbye' } ] }           | <div class="section"><span class="greeting">Hello</span></div><div class="section"><span class="greeting">Goodbye</span></div> |

  Scenario Outline: match and inject empty multiple value container should remove the element
    Given the HTML fragment "<div><ul><li class='character'></li></ul></div>"
      When <data> is injected
      Then the HTML fragment is <transformed>
      
    Examples:
      | data                       | transformed          |
      | { 'ul' => [] }             | <div></div>          |
      | { 'ul' => { 'li' => [] } } | <div><ul></ul></div> |
  
  Scenario Outline: match and inject data into element attributes
    Given the HTML fragment "<a href='#'>my link</a>"
      When <data> is injected
      Then the HTML fragment is <transformed>
    
    Examples:
      | data | transformed |
      | { 'a' => { '@@href' => 'http://www.example.com' } } | <a href="http://www.example.com">my link</a> |
      | { 'a' => { 'href' => 'http://www.example.com' } }   | <a href="#">my link</a>                      |
      | { 'a' => { '@@style' => 'color:red' } }             | <a href="#" style="color:red">my link</a>    |
      | { 'a @@href' => 'http://www.example.com' }          | <a href="http://www.example.com">my link</a> |
      
  Scenario: when doing an attribute match, special 'content' attribute should change the inner_html
    Given the HTML fragment "<a href='#'>my link</a>"
      When { 'a' => { '@@href' => 'http://www.example.com', Nate::Engine::CONTENT_ATTRIBUTE => 'example.com' } } is injected
      Then the HTML fragment is <a href="http://www.example.com">example.com</a>
      
  Scenario: special 'content' attribute should be able to be transformed
    Given the HTML fragment "<div id='x'><p>Hi</p></div>"
      When { 'div' => { '@@id' => 'y', Nate::Engine::CONTENT_ATTRIBUTE => { 'p' => 'Bye' } } } is injected
      Then the HTML fragment is <div id='y'><p>Bye</p></div>
      
  Scenario: multiple value matches shouldn't leak from one value to the next
    Given the HTML fragment "<a href='#'>link</a>"
      When { 'a' => [ { '@@href' => 'x' }, 'new link' ] } is injected
      Then the HTML fragment is <a href="x">link</a><a href="#">new link</a>
      
  Scenario Outline: matches on multiple items should inject into all matches
    Given the HTML fragment "<h1>First Header</h1><h2>Second Header</h2><h1>Third Header</h1>"
      When <data> is injected
      Then the HTML fragment is <transformed>

    Examples:
      | data                             | transformed |
      | { 'h1' => 'New Header' }         | <h1>New Header</h1><h2>Second Header</h2><h1>New Header</h1> |
      | { 'h1' => [ 'Hello', 'There' ] } | <h1>Hello</h1><h1>There</h1><h2>Second Header</h2><h1>Hello</h1><h1>There</h1> |
       
  Scenario Outline: value can be anything that has a string representation
    Given the HTML fragment "<a href='#'></a>"
      When <data> is injected
      Then the HTML fragment is <transformed>
      
    Examples:
      | data                         | transformed              |
      | { 'a' => { '@@href' => 1 } } | <a href='1'></a>         |
      | { 'a' => 'click me' }        | <a href="#">click me</a> |
    
  Scenario: use a file rather than a string as source input
    Given the file "features/support/file.html"
      When { 'h1' => 'Monkey in a file' } is injected
      Then the HTML fragment is <h1>Monkey in a file</h1>
  
  Scenario: should be able inject in multiple steps
    Given the HTML fragment "<div id='data'></div>"
      When { '#data' => Nate::Engine.from_string('<span></span>')} is injected
      And { 'span' => 'hello' } is injected sometime later
      Then the HTML fragment is <div id='data'><span>hello</span></div>
      
  Scenario: injection shouldn't modify the original template, only create a new version with changes
    Given the HTML fragment "<h1>Hi</h1>"
      When { 'h1' => 'Bye' } is injected
      Then the original HTML fragment is <h1>Hi</h1>
  
  Scenario Outline: should be able to create a new template from content in an existing template 
    Given the HTML fragment "<div id='header'>Header</div><div id='content'><h1>Content</h1></div>"
      When <data> is selected
      Then the HTML fragment is <transformed>
    
    Examples:
      | data           | transformed                                                           |
      | "#content > *" | <h1>Content</h1>                                                      |
      | "#content"     | <div id='content'><h1>Content</h1></div>                              |
      | "div"          | <div id='header'>Header</div><div id='content'><h1>Content</h1></div> |
      
  Scenario Outline: should be able to select all content including text nodes when doing a select
    Given the HTML fragment "<div id='header'>header text</div><div id='content'>content text</div><div id='footer'><h1>footer</h1></div>"
      When <data> is selected
      Then the HTML fragment is <transformed>
      
    Examples:
      | data        | transformed                            |
      | "###header" | header text                            |
      | "##div"     | header textcontent text<h1>footer</h1> |
      
   Scenario: should be able to use a nate template as a value when injecting
     Given the HTML fragment "<div id='header'>Header</div><div id='content'></div>"
       When { '#content' => Nate::Engine.from_string( '<h1>Hello</h1>' ) } is injected
       Then the HTML fragment is <div id='header'>Header</div><div id='content'><h1>Hello</h1></div>
       
  Scenario: matches should work on self closing tags
    Given the HTML fragment "<div/>"
      When { 'div' => 'hi' } is injected
      Then the HTML fragment is <div>hi</div>
      
  Scenario: matches on multiple items should inject into all matches when using self closing tags
    Given the HTML fragment "<div/><div/>"
      When { 'div' => 'hi' } is injected
      Then the HTML fragment is <div>hi</div><div>hi</div>
      
  Scenario Outline: should match in namespaces
    Given the HTML fragment "<html xmls='http://www.w3.org/1999/xhtml'><body><div id='header'>header</div></body></html>"
      When <data> is selected
      Then the HTML fragment is <transformed>
      
    Examples:
      | data | transformed |
      | "body" | <body><div id='header'>header</div></body> |
      | "#header" | <div id='header'>header</div> |
    
  Scenario Outline: should inject with namespaces
    Given the HTML fragment "<html xmls='http://www.w3.org/1999/xhtml'><body><div id='header'>header</div></body></html>"
      When <data> is inject
      Then the HTML fragment is <transformed>  
    
      | data                 | transformed |
      | { 'body' => 'hi' }   | <html xmls='http://www.w3.org/1999/xhtml'><body>hi</body></html> |
      | { '#header' => 'bye' | <html xmls='http://www.w3.org/1999/xhtml'><body><div id='header'>bye</div></body></html> |      


