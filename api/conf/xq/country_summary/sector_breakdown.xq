declare variable $country_code external;

let $db             := db:open('iati')
let $country_budget := sum($db//iati-activity[recipient-country/@code=$country_code][reporting-org/@ref='GB-1']/budget/value)
let $sectors        := for $activity in $db//iati-activity[recipient-country/@code=$country_code][reporting-org/@ref='GB-1'][@hierarchy=2]
                       let $budget  := sum($activity/budget/value/text())
                       return for $sector in $activity/sector
                              let $percent               := ($sector/@percentage, 100)[1]
                              let $pct_of_budget         := ($budget idiv 100) * $percent
                              let $pct_of_country_budget := (($pct_of_budget) div $country_budget) * 100
                              return <sector code="{ $sector/@code }">
                                { $pct_of_country_budget }
                              </sector>
return json:serialize(
  <json arrays="json" objects="sector" strings="code" numbers="percentage">
    {
      for $sector in $sectors
      let $code := $sector/@code
      group by $code
      order by sum($sector) descending
      return <sector >
        <percentage>{ sum($sector) } </percentage>
        <code> { $code } </code>
      </sector>  
    } 
  </json> )          
          