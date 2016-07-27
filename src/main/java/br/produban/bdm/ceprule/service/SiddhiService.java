package br.produban.bdm.ceprule.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.produban.bdm.ceprule.enumeration.Condition;
import br.produban.bdm.ceprule.enumeration.FieldType;
import br.produban.bdm.ceprule.enumeration.ItemType;
import br.produban.bdm.ceprule.enumeration.Operator;
import br.produban.bdm.ceprule.model.CepRule;
import br.produban.bdm.ceprule.model.CepRuleItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Created by pedrozatta
 */

@Service
public class SiddhiService {

	final static Logger logger = Logger.getLogger(SiddhiService.class);

	public String generateSiddhi(final CepRule cepRule) {
		String result = freemarker(cepRule);
		logger.info(result);
		return result;
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

		// try {
		// FileTemplateLoader ftl1 = new FileTemplateLoader(new
		// File("src/main/resources/"));
		// MultiTemplateLoader mtl = new MultiTemplateLoader(new
		// TemplateLoader[] { ftl1 });
		// cfg.setTemplateLoader(mtl);
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }

		try {
			Template template = cfg.getTemplate("siddhi.ftl");

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("CEP_RULE", cepRule);
			try {
				BigDecimal value = new BigDecimal(cepRule.getField(CepRule.FIELD_VALUE).getValueMin());
				data.put(CepRule.FIELD_VALUE, value);
			} catch (NullPointerException e) {
				data.put(CepRule.FIELD_VALUE, BigDecimal.ZERO);
			}
			data.put("alias", "Entrada" + WordUtils.capitalize(cepRule.getTool().getNickName()));
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
