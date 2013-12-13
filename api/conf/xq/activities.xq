declare variable $start external;
declare variable $limit external;

declare variable $reporting-org external;

let $filter := "$db//iati-activity"
let $filter := concat($filter, 
	switch($reporting-org)
		case "" return ""
		default return "[reporting-org/@ref=$reporting-org]")

let $db 	    := db:open('iati')
let $bindings   := map { '$db' := $db,  '$reporting-org' := $reporting-org }
let $activities := for $activity in xquery:eval($filter, $bindings)
				   order by $activity/iati-identifier
				   return $activity
return <result>
	<ok>true</ok>
	<iati-activities generated-datetime="{ current-dateTime() }">
		<query>
			<total-count>{ count($activities) }</total-count>
			<start>{ $start }</start>
			<limit>{ $limit }</limit>
		</query>
		{ subsequence($activities, $start, $limit) }
	</iati-activities>
</result>