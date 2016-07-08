package br.produban.services;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.produban.enumerations.FieldType;
import br.produban.enumerations.ItemType;
import br.produban.enumerations.Operator;
import br.produban.models.CepRule;
import br.produban.models.CepRuleItem;
import br.produban.repositories.CepRuleMongoRepository;

@Service
public class CepRuleService {

	final static Logger logger = Logger.getLogger(CepRuleService.class);

	@Autowired
	private CepRuleMongoRepository cepRuleRepository;

	public CepRule normalize(final CepRule cepRule) {

		for (CepRuleItem item : cepRule.getChilds()) {
			normalizeCepRuleItem(cepRule, item);
		}

		return cepRule;

	}

	public CepRule normalizeCepRuleItem(final CepRule cepRule, CepRuleItem item) {
		switch (ItemType.fromExternal(item.getType())) {
		case GROUP:
			normalizeGroup(cepRule, item);
			break;
		case CONDITION:
			normalizeCondition(cepRule, item);
			break;
		}
		return cepRule;
	}

	public void normalizeGroup(final CepRule cepRule, CepRuleItem group) {
		for (CepRuleItem cepRuleItem : group.getChilds()) {
			normalizeCepRuleItem(cepRule, cepRuleItem);
		}
	}

	public void normalizeCondition(final CepRule cepRule, CepRuleItem condition) {
		if (StringUtils.isEmpty(condition.getFieldType())) {
			condition.setFieldType(FieldType.STRING.external);
		}
	}

	public String generateSiddhi(final CepRule cepRule) {

		StringBuilder sb = new StringBuilder("from Entrada");

		sb.append(WordUtils.capitalize(cepRule.getTool()));
		sb.append(" [");

		CepRuleItem group = new CepRuleItem();
		group.setChilds(cepRule.getChilds());

		processGroup(sb, cepRule, group);

		sb.append("]");

		String result = sb.toString();
		logger.info(result);
		return result;

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
		// TODO TRATAR OR
		if (sb.lastIndexOf("AND ") == sb.length() - 4) {
			sb.insert(sb.lastIndexOf("AND "), " ) ");
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

	public CepRule save(final String user, final CepRule value) {
		if (StringUtils.isEmpty(user)) {
			throw new IllegalArgumentException("User can not be null");
		}
		if (value == null) {
			throw new IllegalArgumentException("CepRule can not be null");
		}
		CepRule cepRule = this.normalize(value);
		if (StringUtils.isEmpty(cepRule.getCepRuleId())) {
			cepRule.setCreatedDate(now());
			cepRule.setCreatedBy(user);
		} else {
			CepRule cepRuleOld = cepRuleRepository.findOne(cepRule.getCepRuleId());
			cepRule.setCreatedDate(cepRuleOld.getCreatedDate());
			cepRule.setCreatedBy(cepRuleOld.getCreatedBy());
		}
		cepRule.setChangedDate(now());
		cepRule.setChangedBy(user);

		cepRule = cepRuleRepository.save(cepRule);

		String siddhi = generateSiddhi(cepRule);
		cepRule.setSiddhi(siddhi);

		return cepRule;
	}

	protected Date now() {
		return Calendar.getInstance().getTime();
	}

	public Iterable<CepRule> findAll() {
		logger.info("listCepRules()");
		return cepRuleRepository.findAll();
	}

	public CepRule findOne(String id) {
		logger.info("getCepRule() " + id);
		CepRule cepRule = cepRuleRepository.findOne(id);
		return cepRule;
	}

}
