declare variable $country_code external;
declare variable $start_date external;
declare variable $end_date external;


let $db                 := db:open('iati')
let $budegetsForCountry := for $budget in  $db//iati-activity[reporting-org/@ref='GB-1'][recipient-country/@code=$country_code][budget/period-start[@iso-date>$start_date]][budget/period-start[@iso-date<$end_date]]
          let $group := data($budget/recipient-country/@code)
          group by $group
          return 
          	json:serialize( 
          		<json objects="json" numbers="totalBudget">
          			<totalBudget>{xs:integer(sum($budget/budget/value))}</totalBudget>
      			</json>
  			)

return $budegetsForCountry