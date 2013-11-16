|  START  b=node:entities(type="budget")
      |  MATCH  v-[:value]-b-[:budget]-component-[:`related-activity`]-proj,
      |         component-[:`reporting-org`]-org,
      |         b-[:`period-start`]-period
      |  WHERE  proj.type = 1
      |  AND    org.ref   = "GB-1"
      |  RETURN proj.ref          as projectId,
      |         v.value           as value,
      |         period.`iso-date` as date