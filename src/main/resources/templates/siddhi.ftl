[#ftl]
[#macro formatMessage]str:concat("[#list message as item][#if !item.type?? || item.type != "new"] ", [/#if]${item.name}[#if !item.type?? || item.type != "new"], " [/#if][/#list]")[/#macro]
[#macro formatFilter]${filter}[/#macro]

@Plan:name('${CEP_RULE_NAME}')

[#if CEP_RULE.description??]@Plan:description('${CEP_RULE.description}')[/#if]

/* define streams/tables and write queries here ... */
@Import('${IN_STREAM.id}')
define stream ${IN_STREAM.alias} ( [#list IN_STREAM.fields as field]${field.name} ${field.type.external}[#if field?has_next], [/#if][/#list] );

@Export('${OUT_STREAM.id}')
define stream ${OUT_STREAM.alias} ( [#list OUT_STREAM.fields as field]${field.name} ${field.type.external}[#if field?has_next], [/#if][/#list] );

from ${IN_STREAM.alias}[#if WINDOW_LENGTH??]#window.length(${WINDOW_LENGTH.value})[/#if] [ [@formatFilter /] ]
select  "${CEP_RULE.situation}" as situation, csId as id, [@formatMessage /] as message, hostname as hostname, fileSystem as item, value as value, "open" as eventstatus, "critical" as severity, [#if value??]${value.valueMin}[/#if] as threshold, businessService as businessService, technicalService as technicalService, serviceComponent as serviceComponent, environment as environment, lsFunction as lsFunction, hyperName as hyperName, csSite as csSite, platform as platform, rule as rule, status as status, timestamp as timestamp, tool as tool
[#if groupBy?has_content]
groupBy [#list groupBy as item]${item.name}[#if item?has_next], [/#if][/#list]
[/#if]

insert into ${OUT_STREAM.alias};
