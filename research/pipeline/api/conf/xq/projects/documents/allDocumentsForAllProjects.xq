(: A query to get all the documents for a projects :)

let $db      := db:open('iati')
let $allProjectDocuments := for $project in $db//iati-activity[@hierarchy=1]
                             
                                let $documents := for $document in $project//document-link
                                   let $categories := for $theCategory in $document//category
                                                        return <category>{ data($theCategory) }</category>  
                                    return ( <document>
                                                <title>{data($document//title)}</title>
                                                <url>{data($document/@url)}</url>
                                                <format>{data($document/@format)}</format>
                                                <categories>
                                                    {$categories}
                                                </categories>
                                             </document>   )        
                              
                         return (
                           <project>
                                <iatiId>{data($project//iati-identifier)}</iatiId>
                                <title>{data($project//title)}</title>
                                <participating-orgs>{data($project//participating-org)}</participating-orgs>
                                <recipient></recipient>
                                <documents>{$documents}</documents>
                           </project> )                            


return json:serialize(
            <json arrays="json documents categories participating-org" objects="project document" strings="iatiId title recipient url format category" >
              {$allProjectDocuments} 
            </json> )   