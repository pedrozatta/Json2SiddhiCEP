[#ftl]
[#macro formatMessage][#if message?size > 1]str:concat([/#if][#list message as item][#if item.type?? && item.type = "new"]"${item.name}"[#else]${item.name}[/#if][#if !item?is_last],[/#if][/#list][#if message?size > 1])[/#if] as message[/#macro]
[#macro formatFilter]${filter}[/#macro]
@Plan:name('${CEP_RULE_NAME}')
[#escape]
[#if CEP_RULE.description??]@Plan:description('${CEP_RULE.description}')[/#if]

@Import('${IN_STREAM.id}')
define stream ${IN_STREAM.alias} ( [#list IN_STREAM.fields as field]${field.name} ${field.type.external}[#if field?has_next], [/#if][/#list] );

@Export('${OUT_STREAM.id}')
define stream ${OUT_STREAM.alias} ( [#list OUT_STREAM.fields as field]${field.name} ${field.type.external}[#if field?has_next], [/#if][/#list] );

from ${IN_STREAM.alias}[#if CEP_RULE.window??]#${CEP_RULE.window.name}(${CEP_RULE.window.value})[/#if] [ [@formatFilter /] ]
select  "${CEP_RULE.situation}" as situation, csId as id, [@formatMessage /], hostname as hostname, fileSystem as item, value as value, "open" as eventstatus, "critical" as severity, [#if value??]${value.valueMin}[/#if] as threshold, businessService as businessService, technicalService as technicalService, serviceComponent as serviceComponent, environment as environment, lsFunction as lsFunction, hyperName as hyperName, csSite as csSite, platform as platform, rule as rule, status as status, timestamp as timestamp, tool as tool
[#if groupBy?has_content]
groupBy [#list groupBy as item]${item.name}[#if item?has_next], [/#if][/#list]
[/#if]

insert into ${OUT_STREAM.alias};
[/#excape]