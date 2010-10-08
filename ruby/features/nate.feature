Feature:

 As someone working on a dynamic website
 In order to keep the HTML free from logic
 I want 'nate' to transform plain HTML based on data I supply

 Scenario Outline: match and inject data
   Given the plain HTML fragment "<ul><li class='character'></li></ul>"
    When <data> is injected
    Then the plain HTML fragment is <transformed>

   Examples:
     | data                                            | transformed |
     | {'.character' => 'Leonard'}                     | <ul><li class="character">Leonard</li></ul> |
     | {'.character' => ['Leonard','Sheldon','Penny']} | <ul><li class="character">Leonard</li><li class="character">Sheldon</li><li class="character">Penny</li></ul> |

  Scenario Outline: match and inject a single data value
    Given the plain HTML fragment "<div class='section'><span class='content'></span></div>"
     When <data> is injected
     Then the plain HTML fragment is <transformed>
    
    Examples:
      | data                             | transformed                                                           |
      | {'.section' => 'Hello Section' } | <div class="section">Hello Section</div>                              |
      | {'.content' => 'Hello Content'}  | <div class="section"><span class="content">Hello Content</span></div> |
