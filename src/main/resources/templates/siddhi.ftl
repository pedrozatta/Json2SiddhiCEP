[#ftl]
[#macro formatMessage]str:concat("[#list message as item][#if item.type != "new"]", [/#if]${item.name}[#if item.type != "new"], "[/#if][/#list]")[/#macro]
from ${alias}[#if WINDOW_LENGTH??]#window.length(${WINDOW_LENGTH.value})[/#if] [ ${filter} ]
select  "${CEP_RULE.situation}" as situation, csId as id, [@formatMessage /] as message, hostname as hostname, fileSystem as item, value as value, "open" as eventstatus, "critical" as severity, ${value?string("0.000")} as threshold, businessService as businessService, technicalService as technicalService, serviceComponent as serviceComponent, environment as environment, lsFunction as lsFunction, hyperName as hyperName, csSite as csSite, platform as platform, rule as rule, status as status, timestamp as timestamp, tool as tool
[#if groupBy?has_content]
groupBy [#list groupBy as item]${item.name}[#if item?has_next], [/#if][/#list]
[/#if]
insert into Saida;