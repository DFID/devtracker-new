declare variable $country_code external;

let $db      := db:open('iati')
let $budgets := for $budget in $db//iati-activity[recipient-country/@code=$country_code][reporting-org/@ref='GB-1']/budget
                let $date       := $budget/period-start/@iso-date
                let $date_parts := tokenize($date, '-')
                let $month      := number($date_parts[position() = 2])
                let $year       := if ($month < 4)
                                   then number($date_parts[position() = 1]) - 1
                                   else number($date_parts[position() = 1])
                let $fy         := concat("FY", substring(string($year), 3), "/", substring(string($year+1), 3))
                group by $fy
                order by $fy
                return <financial-year>
                  <label>{ $fy }</label>
                  <value>{ sum($budget/value/text()) }</value>
                </financial-year>
return json:serialize(
  <json arrays="json" objects="financial-year" strings="label">
    { $budgets }
  </json>)
