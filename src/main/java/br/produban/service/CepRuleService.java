package br.produban.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import br.produban.models.CepRule;
import br.produban.models.CepRuleItem;

@Service
public class CepRuleService {

	final static Logger logger = Logger.getLogger(CepRuleService.class);

	public String processCepRule(final CepRule cepRule) {

		StringBuilder sb = new StringBuilder("from EntradaZabbix [");

		CepRuleItem group = new CepRuleItem();
		group.setChilds(cepRule.getChilds());

		processGroup(sb, cepRule, group);

		// sb.append(
		// "platform == \"Linux\" and metric == \"system.dsk.utilization\" and
		// fileSystem == \"/ArquitecturaE-business\" and tool == \"Zabbix\" and
		// value >= 95 and lsFunction == \"Application Server\" and (scName ==
		// \"MODULO_1_ESTRUCTURAL_(NMW)\" or scName ==
		// \"MODULO_2_ESTRUCTURAL_(NMW)\" or scName ==
		// \"MODULO_3_ESTRUCTURAL_(NMW)\")");

		sb.append("]");

		String result = sb.toString();
		logger.info(result);
		return result;

	}

	protected void processGroup(StringBuilder sb, final CepRule cepRule, CepRuleItem group) {

		sb.append(" ( ");
		for (CepRuleItem item : group.getChilds()) {
			switch (item.getType()) {
			case CepRuleItem.TYPE_GROUP:
				processGroup(sb, cepRule, item);
				break;
			case CepRuleItem.TYPE_CONDITION:
				processCondition(sb, cepRule, group, item);
				break;
			default:
				logger.error("Invalid or not defined Type !");
				break;
			}
		}
		sb.append(" ) ");

	}

	protected void processCondition(StringBuilder sb, final CepRule cepRule, CepRuleItem group, CepRuleItem condition) {
		sb.append(condition.getField());
		sb.append(" ");
		sb.append(condition.getOperator());
		sb.append(" '");
		sb.append(condition.getValueMin());
		sb.append("' ");
		if (CepRuleItem.OPERATOR_BETWEEN.equals(condition.getOperator())) {
			sb.append("'");
			sb.append(condition.getValueMax());
			sb.append("' ");
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

}
