declare variable $country_code := 'BD';
let $db        := db:open('iati')

let $db := $db//iati-activity[reporting-org/@ref='GB-1'][recipient-country/@code=$country_code]
let $projects := for $project in $db
                  let $code := data($project//iati-identifier)
                  let $par := for $partOrg in $project//participating-org
                            return for $partOrgData in $partOrg
                                    where data($partOrgData) != 'UNITED KINGDOM'
                                   return  <participatingName>{data($partOrg)}</participatingName>                           
                                          
                return  <count>{count($code)}</count>
                                    
return json:serialize( 
          <json objects="json" numbers="count"> 
            { $projects } 
          </json>)
