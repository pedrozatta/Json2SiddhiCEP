package br.produban.services;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.produban.enumerations.Condition;
import br.produban.enumerations.FieldType;
import br.produban.enumerations.ItemType;
import br.produban.models.CepRule;
import br.produban.models.CepRuleItem;
import br.produban.repositories.CepRuleMongoRepository;

/**
 * Created by pedrozatta
 */

@Service
public class CepRuleService {

	final static Logger logger = Logger.getLogger(CepRuleService.class);

	@Autowired
	private SiddhiService siddhiService;

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
		if (StringUtils.isEmpty(condition.getCondition())) {
			condition.setCondition(Condition.AND.external);
		}
	}

	public CepRule save(final String user, final CepRule value) {
		Validate.notEmpty(user);

		// if (StringUtils.isEmpty(user)) {
		// throw new IllegalArgumentException("User can not be null");
		// }
		if (value == null) {
			throw new IllegalArgumentException("CepRule can not be null");
		}
		CepRule cepRule = this.normalize(value);
		if (StringUtils.isEmpty(cepRule.getCepRuleId())) {
			cepRule.setCreatedDate(now());
			cepRule.setCreatedBy(user);
			this.populateSituation(cepRule);
		} else {
			CepRule cepRuleOld = cepRuleRepository.findOne(cepRule.getCepRuleId());
			cepRule.setCreatedDate(cepRuleOld.getCreatedDate());
			cepRule.setCreatedBy(cepRuleOld.getCreatedBy());
			cepRule.setSituation(cepRuleOld.getSituation());
		}
		cepRule.setChangedDate(now());
		cepRule.setChangedBy(user);

		String siddhi = siddhiService.generateSiddhi(cepRule);
		cepRule.setSiddhi(siddhi);

		cepRule = cepRuleRepository.save(cepRule);
		return cepRule;
	}

	protected void populateSituation(CepRule cepRule) {
		String situationPrefix = this.generateSituationPrefix(cepRule);
		List<CepRule> list = this.findBySituation(situationPrefix);
		if (CollectionUtils.isEmpty(list)) {
			cepRule.setSituation(situationPrefix + "_1");
			return;
		}

		Collections.sort(list, new Comparator<CepRule>() {
			@Override
			public int compare(CepRule x, CepRule y) {
				return x.getSituation().compareTo(y.getSituation());
			}
		});

		int x = 0;
		for (CepRule item : list) {
			String situation = item.getSituation();
			try {
				int y = Integer.parseInt(situation.substring(situation.lastIndexOf("_") + 1, situation.length()));
				if (y > x) {
					x = y;
				}
			} catch (java.lang.NumberFormatException e) {
			}
		}

		// String situation = list.get(list.size() - 1).getSituation();
		try {
			// int i =
			// Integer.parseInt(situation.substring(situation.lastIndexOf("_") +
			// 1, situation.length()));
			// i++;
			int i = ++x;
			cepRule.setSituation(situationPrefix + "_" + i);
		} catch (java.lang.NumberFormatException e) {
			cepRule.setSituation(situationPrefix + "_1");
		}
	}

	protected String generateSituationPrefix(CepRule cepRule) {
		String situation = cepRule.getTool();
		CepRuleItem item = cepRule.getField("metric");
		if (item != null) {
			situation += "_metric_" + item.getValueMin();
		}
		situation = situation.replaceAll("[^a-zZ-Z1-9_]", "_");
		return situation;
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

	public List<CepRule> findBySituation(String situation) {
		logger.info("findBySituation() " + situation);
		List<CepRule> list = cepRuleRepository.findBySituationStartingWith(situation);
		return list;
	}

}
