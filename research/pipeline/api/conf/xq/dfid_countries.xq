let $db        := db:open('iati')
let $countries := for $country in $db//iati-activity[reporting-org/@ref='GB-1']/recipient-country
                  let $code := $country/@code
                  let $name := $country/text()
                  group by $code
                  return <country>
                           <code>{ $code }</code>
                           <name>{ $name[1] }</name>
                         </country>
return json:serialize(
   <json arrays="json" objects="country">
     { $countries }
   </json>)
