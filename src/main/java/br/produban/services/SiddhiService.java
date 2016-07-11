package br.produban.services;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import br.produban.enumerations.FieldType;
import br.produban.enumerations.ItemType;
import br.produban.enumerations.Operator;
import br.produban.models.CepRule;
import br.produban.models.CepRuleItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class SiddhiService {

	final static Logger logger = Logger.getLogger(SiddhiService.class);

	public String generateSiddhi(final CepRule cepRule) {
		StringBuilder sb = new StringBuilder();

		// generateRule(sb, cepRule);
		// generateQuery(sb, cepRule);
		// String result = sb.toString();

		String result = freemarker(cepRule);

		logger.info(result);
		return result;
	}

	@Deprecated
	public void generateQuery(final StringBuilder sb, final CepRule cepRule) {

		CepRuleItem value = cepRule.getField("value");
		if (value == null) {
			throw new IllegalArgumentException("Field 'value' not found");
		}

		sb.append("select ");

		sb.append("\"sc_remedy_so_ux_disk_p_c_bigdata\" as situation");
		sb.append(", csId as id");
		sb.append(", str:concat(\"O consumo de Disco ou FS está acima do threshold estipulado em " + value.getValueMin()
				+ "%. Disco ou FS: \", fileSystem, \". Valor atual: \", value, \"%\" ) as message");
		sb.append(", hostname as hostname");
		sb.append(", fileSystem as item");
		sb.append(", value as value");
		sb.append(", \"open\" as eventstatus");
		sb.append(", \"critical\" as severity");
		sb.append(", " + value.getValueMin() + " as threshold");
		sb.append(", businessService as businessService");
		sb.append(", technicalService as technicalService");
		sb.append(", serviceComponent as serviceComponent");
		sb.append(", environment as environment");
		sb.append(", lsFunction as lsFunction");
		sb.append(", hyperName as hyperName");
		sb.append(", csSite as csSite");
		sb.append(", platform as platform");
		sb.append(", rule as rule");
		sb.append(", status as status");
		sb.append(", timestamp as timestamp");
		sb.append(", tool as tool");
		sb.append("\r\n insert into Saida;");

	}

	@Deprecated
	public void generateRule(final StringBuilder sb, final CepRule cepRule) {

		sb.append("from Entrada");

		sb.append(WordUtils.capitalize(cepRule.getTool()));
		sb.append(" [");

		CepRuleItem group = new CepRuleItem();
		group.setChilds(cepRule.getChilds());

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

		StringBuilder sb = new StringBuilder();

		CepRuleItem group = new CepRuleItem();
		group.setChilds(cepRule.getChilds());

		processGroup(sb, cepRule, group);

		if (sb.lastIndexOf("AND ") == sb.length() - 4) {
			sb.delete(sb.length() - 4, sb.length());
		} else if (sb.lastIndexOf("OR ") == sb.length() - 2) {
			sb.insert(sb.lastIndexOf("OR "), " ) ");
		} else {
			sb.append(" ) ");
		}

		sb.delete(0, 3);
		sb.delete(sb.length() - 4, sb.length());

		return sb.toString();

	}

	protected void processGroup(StringBuilder sb, final CepRule cepRule, CepRuleItem group) {

		sb.append(" ( ");
		for (CepRuleItem item : group.getChilds()) {
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
		} else if (sb.lastIndexOf("OR ") == sb.length() - 2) {
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
			switch (condition.getCondition()) {
			case CepRuleItem.CONDITION_AND:
				sb.append("AND ");
				break;
			case CepRuleItem.CONDITION_OR:
				sb.append("OR ");
				break;
			}
		}

	}

	protected String freemarker(CepRule cepRule) {

		// Freemarker configuration object
		Configuration cfg = new Configuration();
		try {
			// Load template from source folder
			Template template = cfg.getTemplate("src/main/resources/siddhi.ftl");

			// Build the data-model
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("CEP_RULE", cepRule);
			data.put("value", cepRule.getField("value").getValueMin());
			data.put("situation", generateSituation(cepRule));
			data.put("alias", "Entrada" + WordUtils.capitalize(cepRule.getTool()));
			data.put("filtro", generateFilter(cepRule));

			Writer out = new StringWriter();
			template.process(data, out);

			String siddhi = out.toString();
			logger.info("freemarker: " + siddhi);

			return siddhi;

		} catch (IOException | TemplateException e) {
			throw new RuntimeException("Freemarker Error", e);
		}
	}

	protected String generateSituation(CepRule cepRule) {
		String situation = cepRule.getTool();
		CepRuleItem item = cepRule.getField("metric");
		if (item != null) {
			situation += "_metric_" + item.getValueMin();
		}
		situation = situation.replaceAll("[^a-zZ-Z1-9_]", "_");
		return situation;
	}

}
