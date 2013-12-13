declare variable $country_code external;

let $db      := db:open('iati')
let $locations := for $activity in $db//iati-activity[location[administrative/@country=$country_code]]
                    for $location in $activity//location
                    return <location>
                      <name>{ $location/name/text() }</name>
                      <title>{ $activity/title/text() }</title>
                      <id>{ $activity/iati-identifier/text() }</id>
                      <latitude>{ data($location/coordinates/@latitude) }</latitude>
                      <longitude>{ data($location/coordinates/@longitude) }</longitude>
                      <precision>{ data($location/coordinates/@precision) }</precision>
                      <type>{ data($location/location-type/@code) }</type>
                    </location>
return json:serialize(
  <json arrays="json" objects="location" numbers="latitude longitude precision">
    { $locations }
  </json>)