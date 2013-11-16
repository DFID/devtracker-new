declare variable $country_code := 'BD';
let $db        := db:open('iati')

let $db := $db//iati-activity[@hierarchy="2"][reporting-org/@ref='GB-1'][recipient-country/@code=$country_code]
let $globalProjects := for $gp in $db
						where ($gp//recipient-region = "Administrative/Capital (AC)"
    							or $gp//recipient-region = "Non Specific Country (NS)"
    							or $gp//recipient-region = "Multilateral Organisation (ZZ)")
							and $gp//related-activity=1
						return  <globalProject>
									<id>{data($gp//related-activity/@ref)}</id> 
									<region>{data($gp//recipient-region)}</region>
								</globalProject>
return json:serialize( 
          	<json arrays="json" objects="globalProject" strings="id region"> 
          		{$globalProjects}
      		</json>
  		)