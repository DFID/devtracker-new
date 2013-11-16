declare variable $country_code external;

declare variable $type_number 				   			  := 1;
declare variable $activity_status_active_number_sequence  := (1,2,3);

let $db       := db:open('iati')

let $projects := $db//iati-activity[recipient-country/@code=$country_code][related-activity[@type=$type_number]]
let $active   := $projects[activity-status[@code = $activity_status_active_number_sequence]]

return json:serialize(
  <json objects="json projects" numbers="active total">{
		<projects>
		  <active>{ count($projects) }</active>
		  <total>{ count($active) }</total>
		</projects>
  }
  </json>
)