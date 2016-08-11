[#ftl]
[#macro formatFilter]${filter}[/#macro]

[#macro formatMessage]
[@compress single_line=true]
[#if message?size > 1]
str:concat([/#if]
[#list message as item][#if item.type?? && item.type = "new"]"${item.name}"
[#else]
${item.name}
[/#if]
[#if !item?is_last], [/#if]
[/#list]
[#if message?size > 1])[/#if]
 [/@compress]
[/#macro]

[#macro getOutputValue list name default]
[@compress single_line=true]

[#list list as item]
	[#if item.name = name]
		${name}
		[#return]
	[/#if]
[/#list]

${default}

[/@compress]
[/#macro]
@Plan:name('${CEP_RULE_NAME}')

[#if CEP_RULE.description??]@Plan:description('${CEP_RULE.description}')[/#if]

@Import('${IN_STREAM.id}')
define stream ${IN_STREAM.alias} ( [#list IN_STREAM.fields as field]${field.name} ${field.type.external}[#if field?has_next], [/#if][/#list] );

@Export('${OUT_STREAM.id}')
define stream ${OUT_STREAM.alias} ( [#list OUT_STREAM.fields as field]${field.name} ${field.type.external}[#if field?has_next], [/#if][/#list] );

from ${IN_STREAM.alias}[#if CEP_RULE.window??]#${CEP_RULE.window.name}(${CEP_RULE.window.value})[/#if] [ [@formatFilter /] ]
[@compress single_line=true]
select  "${CEP_RULE.situation}" as situation
, [@getOutputValue IN_STREAM.fields "csId" "'id'" /] as id
, [@formatMessage /] as message
, [@getOutputValue IN_STREAM.fields "hostname" "'hostname'" /] as hostname
, [@getOutputValue IN_STREAM.fields "fileSystem" "'item'" /] as item
, [@getOutputValue IN_STREAM.fields "value" "0.0" /] as value
, "open" as eventstatus
, "critical" as severity
, [#if value??]${value.valueMin}[#else]0.0[/#if] as threshold
, [@getOutputValue IN_STREAM.fields "businessService" "'businessService'" /] as businessService
, [@getOutputValue IN_STREAM.fields "technicalService" "'technicalService'" /] as technicalService
, [@getOutputValue IN_STREAM.fields "serviceComponent" "'serviceComponent'" /] as serviceComponent
, [@getOutputValue IN_STREAM.fields "environment" "'environment'" /] as environment
, [@getOutputValue IN_STREAM.fields "lsFunction" "'lsFunction'" /] as lsFunction
, [@getOutputValue IN_STREAM.fields "hyperName" "'hyperName'" /] as hyperName
, [@getOutputValue IN_STREAM.fields "csSite" "'csSite'" /] as csSite
, [@getOutputValue IN_STREAM.fields "platform" "'platform'" /] as platform
, [@getOutputValue IN_STREAM.fields "rule" "'rule'" /] as rule
, [@getOutputValue IN_STREAM.fields "status" "'status'" /] as status
, timestamp as timestamp
, tool as tool
[#if groupBy?has_content]

groupBy 
[#list groupBy as item]
${item.name}[#if item?has_next], [/#if]
[/#list]
[/#if]

[/@compress]

insert into ${OUT_STREAM.alias};
