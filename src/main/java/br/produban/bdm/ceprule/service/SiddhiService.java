package br.produban.bdm.ceprule.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.produban.bdm.ceprule.enumeration.Condition;
import br.produban.bdm.ceprule.enumeration.FieldType;
import br.produban.bdm.ceprule.enumeration.ItemType;
import br.produban.bdm.ceprule.enumeration.Operator;
import br.produban.bdm.ceprule.model.CepRule;
import br.produban.bdm.ceprule.model.CepRuleItem;
import br.produban.bdm.ceprule.model.ExecutionPlan;
import br.produban.bdm.ceprule.ws.soap.EventProcessorAdminServiceClient;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Created by pedrozatta
 */

@Service
public class SiddhiService {

	final static Logger logger = Logger.getLogger(SiddhiService.class);

	@Autowired
	private ToolService toolService;

	@Autowired
	private EventProcessorAdminServiceClient eventProcessorAdminServiceClient;

	@Value("${br.produban.wso2.cep.outputStreamId}")
	protected String outputStreamId;

	public ExecutionPlan generateExecutionPlan(final CepRule cepRule) {
		String plan = freemarker(cepRule);
		logger.info(plan);
		ExecutionPlan executionPlan = eventProcessorAdminServiceClient.validateExecutionPlan(plan);
		return executionPlan;
	}

	@Deprecated
	public void generateRule(final StringBuilder sb, final CepRule cepRule) {

		sb.append("from Entrada");

		sb.append(WordUtils.capitalize(cepRule.getTool().getNickName()));
		sb.append(" [");

		CepRuleItem group = new CepRuleItem();
		group.setChildren(cepRule.getChildren());

		processGroup(sb, cepRule, group);

		if (sb.lastIndexOf("AND ") == sb.length() - 4) {
			sb.delete(sb.length() - 4, sb.length());
		} else if (sb.lastIndexOf("OR ") == sb.length() - 2) {
			sb.insert(sb.lastIndexOf("OR "), " ) ");
		} else {
			sb.append(" ) ");
		}

		sb.append("]\r\n");

	}

	public String generateFilter(final CepRule cepRule) {

		if (CollectionUtils.isEmpty(cepRule.getChildren())) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		CepRuleItem group = new CepRuleItem();
		group.setChildren(cepRule.getChildren());

		processGroup(sb, cepRule, group);

		if (sb.lastIndexOf("AND ") == sb.length() - 4) {
			sb.delete(sb.length() - 4, sb.length());
		} else if (sb.lastIndexOf("OR ") == sb.length() - 3) {
			sb.insert(sb.lastIndexOf("OR "), " ) ");
		} else {
			sb.append(" ) ");
		}

		sb.delete(0, 3);
		sb.delete(sb.length() - 4, sb.length());

		return sb.toString();

	}

	protected void processGroup(StringBuilder sb, final CepRule cepRule, CepRuleItem group) {

		if (CollectionUtils.isEmpty(group.getChildren())) {
			return;
		}

		sb.append(" ( ");
		for (CepRuleItem item : group.getChildren()) {
			switch (ItemType.fromExternal(item.getType())) {
			case GROUP:
				processGroup(sb, cepRule, item);
				break;
			case CONDITION:
				processCondition(sb, cepRule, group, item);
				break;
			default:
				logger.error("Invalid or not defined Type !");
				break;
			}
		}

		if (sb.lastIndexOf("AND ") == sb.length() - 4) {
			sb.insert(sb.lastIndexOf("AND "), " ) ");
		} else if (sb.lastIndexOf("OR ") == sb.length() - 3) {
			sb.insert(sb.lastIndexOf("OR "), " ) ");
		} else {
			sb.append(" ) ");
		}

	}

	protected void processCondition(StringBuilder sb, final CepRule cepRule, CepRuleItem group, CepRuleItem condition) {

		if (Operator.fromExternal(condition.getOperator()) == Operator.BETWEEN) {
			sb.append("( ");
			sb.append(condition.getField());
			sb.append(" >= ");
			sb.append(condition.getValueMin());
			sb.append(" AND ");
			sb.append(condition.getField());
			sb.append(" <= ");
			sb.append(condition.getValueMax());
			sb.append(" ) ");
		} else {

			switch (FieldType.fromExternal(condition.getFieldType())) {
			case DOUBLE:
				sb.append(condition.getField());
				sb.append(" ");
				sb.append(condition.getOperator());
				sb.append(" ");
				sb.append(condition.getValueMin());
				sb.append(" ");
				break;
			default:
				sb.append(condition.getField());
				sb.append(" ");
				sb.append(condition.getOperator());
				sb.append(" '");
				sb.append(condition.getValueMin());
				sb.append("' ");
				break;
			}
		}

		if (!StringUtils.isEmpty(condition.getCondition())) {
			switch (Condition.fromExternal(condition.getCondition())) {
			case AND:
				sb.append("AND ");
				break;
			case OR:
				sb.append("OR ");
				break;
			}
		}

	}

	protected String freemarker(CepRule cepRule) {

		Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		cfg.setClassForTemplateLoading(this.getClass(), "/templates");

		try {
			Template template = cfg.getTemplate("siddhi.ftl");

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("IN_STREAM", toolService.findById(cepRule.getTool().getId()));
			data.put("OUT_STREAM", toolService.findById(outputStreamId));

			data.put("CEP_RULE", cepRule);
			data.put(CepRule.FIELD_VALUE, cepRule.getField(CepRule.FIELD_VALUE));
			data.put("CEP_RULE_NAME", cepRule.getRuleName().replaceAll("[^a-zA-Z1-9_]", "_"));
			data.put("filter", generateFilter(cepRule));
			data.put("message", cepRule.getMessage());
			data.put("groupBy", cepRule.getGroupBy());

			data.put("WINDOW_LENGTH", cepRule.getToolBox(CepRule.WINDOW_LENGTH));

			Writer out = new StringWriter();
			template.process(data, out);

			String siddhi = out.toString();
			logger.info("freemarker: " + siddhi);

			return siddhi;

		} catch (IOException | TemplateException e) {
			throw new RuntimeException("Freemarker Error", e);
		}
	}

	protected String generateMessage(String message) {
		Validate.notEmpty(message);

		StringBuilder sb = new StringBuilder();
		sb.append('\"');
		sb.append(message);
		sb.append('\"');

		if (sb.indexOf("{") == -1) {
			return sb.toString();
		}

		int index = sb.indexOf("{");
		while (index != -1) {
			sb.replace(index, index + 1, "\", ");
			index = sb.indexOf("}", index);
			sb.replace(index, index + 1, ", \"");
			index = sb.indexOf("{");
		}
		return sb.toString();
	}

}
