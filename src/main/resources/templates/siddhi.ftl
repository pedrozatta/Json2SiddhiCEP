[#ftl]
[#macro formatMessage]str:concat("[#list message as item][#if !item.type?? || item.type != "new"] ", [/#if]${item.name}[#if !item.type?? || item.type != "new"], " [/#if][/#list]")[/#macro]
[#macro formatFilter]${filter}[/#macro]
/* Enter a unique ExecutionPlan */
@Plan:name('Pedro')

/* Enter a unique description for ExecutionPlan */
-- @Plan:description('ExecutionPlan')

/* define streams/tables and write queries here ... */

@Import('IN_StormToSiddhi_Zabbix:1.0.0')
define stream EntradaZabbix (bsAssetid string, bsCompany string, bsEnv string, bsName string, bsStatus string, bsTarget string, bsWeight string, businessService string, company string, csDelivery string, csId string, csManufacturer string, csModel string, csName string, csNetwork string, csSite string, csType string, distNetwork string, environment string, FDC string, fileSystem string, lsFunction string, hostname string, hyperClusterId string, hyperClusterName string, hyperFQDN string, hyperId string, hyperName string, hyperNetwork string, lsId string, lsItem string, lsOS string, lsPatchLevel string, metric string, platform string, prd string, rule string, scAssetid string, scCompany string, scDel string, scDelivery string, scEnv string, scItem string, scName string, scSiglas string, scStatus string, serviceComponent string, status string, technicalService string, tenant_id string, timestamp string, tool string, tsAssetid string, tsCompany string, tsDel string, tsEnv string, tsName string, tsNoSlaTime string, tsStatus string, tsWeight string, vdc string, value double, processName string, serviceName string);

@Export('OUT_SiddhiToAlerts:1.0.0')
define stream Saida (situation string, id string, message string, hostname string, item string, value double, eventstatus string, severity string, threshold double, businessService string, technicalService string, serviceComponent string, environment string, lsFunction string, hyperName string, csSite string, platform string, rule string, status string, timestamp string, tool string);

from ${alias}[#if WINDOW_LENGTH??]#window.length(${WINDOW_LENGTH.value})[/#if] [ [@formatFilter /] ]
select  "${CEP_RULE.situation}" as situation, csId as id, [@formatMessage /] as message, hostname as hostname, fileSystem as item, value as value, "open" as eventstatus, "critical" as severity, ${value.valueMin} as threshold, businessService as businessService, technicalService as technicalService, serviceComponent as serviceComponent, environment as environment, lsFunction as lsFunction, hyperName as hyperName, csSite as csSite, platform as platform, rule as rule, status as status, timestamp as timestamp, tool as tool
[#if groupBy?has_content]
groupBy [#list groupBy as item]${item.name}[#if item?has_next], [/#if][/#list]
[/#if]
insert into Saida;
