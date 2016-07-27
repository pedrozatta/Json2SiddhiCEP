package br.produban.bdm.ceprule.service;

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

import br.produban.bdm.ceprule.enumeration.Condition;
import br.produban.bdm.ceprule.enumeration.FieldType;
import br.produban.bdm.ceprule.enumeration.ItemType;
import br.produban.bdm.ceprule.model.CepRule;
import br.produban.bdm.ceprule.model.CepRuleItem;
import br.produban.bdm.ceprule.repository.CepRuleRepository;

/**
 * Created by pedrozatta
 */

@Service
public class CepRuleService {

	final static Logger logger = Logger.getLogger(CepRuleService.class);

	@Autowired
	private SiddhiService siddhiService;

	@Autowired
	private CepRuleRepository cepRuleRepository;

	@Autowired
	private UserService userService;

	public CepRule normalize(final CepRule cepRule) {

		for (CepRuleItem item : cepRule.getChildren()) {
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
		CepRuleItem lastCepRuleItem = null;
		for (CepRuleItem cepRuleItem : group.getChildren()) {
			lastCepRuleItem = cepRuleItem;
			normalizeCepRuleItem(cepRule, cepRuleItem);
		}
		if (lastCepRuleItem != null && StringUtils.isNotEmpty(group.getConditionGroup())) {
			lastCepRuleItem.setCondition(group.getConditionGroup());
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

	public CepRule save(final CepRule value) {
		final String user = userService.getAuthenticatedUserName();
		return save(user, value);

	}

	protected CepRule save(final String user, final CepRule value) {
		Validate.notNull(value, "CepRule can not be null");
		Validate.notEmpty(user);
		Validate.notEmpty(value.getMessage());

		CepRule cepRule = this.normalize(value);
		if (StringUtils.isEmpty(cepRule.getCepRuleId())) {
			cepRule.setCepRuleId(String.valueOf(cepRuleRepository.count() + 1));
			cepRule.setCreatedDate(this.now());
			cepRule.setCreatedBy(user);
			this.populateSituation(cepRule);
		} else {
			CepRule cepRuleOld = cepRuleRepository.findOne(cepRule.getCepRuleId());
			checkPrivilegesToUpdate(user, cepRuleOld);
			cepRule.setCreatedDate(cepRuleOld.getCreatedDate());
			cepRule.setCreatedBy(cepRuleOld.getCreatedBy());
			cepRule.setSituation(cepRuleOld.getSituation());
		}
		cepRule.setChangedDate(this.now());
		cepRule.setChangedBy(user);

		String siddhi = siddhiService.generateSiddhi(cepRule);
		cepRule.setSiddhi(siddhi);

		cepRule = cepRuleRepository.save(cepRule);

		return cepRule;
	}

	protected void checkPrivilegesToUpdate(final String user, CepRule cepRule) {
		Validate.isTrue(user.equals(cepRule.getCreatedBy()));
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
