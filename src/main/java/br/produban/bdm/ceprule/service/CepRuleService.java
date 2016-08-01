package br.produban.bdm.ceprule.service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import br.produban.bdm.ceprule.model.Tool;
import br.produban.bdm.ceprule.model.ToolField;
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

	@Autowired
	private ToolService toolService;

	protected Map<String, ToolField> cache;

	public void normalize(final CepRule cepRule) {
		cacheFields(cepRule.getTool().getId());

		if (!CollectionUtils.isEmpty(cepRule.getChildren())) {
			for (CepRuleItem item : cepRule.getChildren()) {
				normalizeCepRuleItem(cepRule, item);
			}
		}

	}

	protected void cacheFields(String toolId) {
		cache = new HashMap<String, ToolField>();
		Tool tool = toolService.findById(toolId);
		if (tool != null && !CollectionUtils.isEmpty(tool.getFields())) {
			for (ToolField field : tool.getFields()) {
				cache.put(field.getName(), field);
			}
		}
	}

	protected CepRule normalizeCepRuleItem(final CepRule cepRule, CepRuleItem item) {
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

	protected void normalizeGroup(final CepRule cepRule, CepRuleItem group) {
		CepRuleItem lastCepRuleItem = null;
		for (CepRuleItem cepRuleItem : group.getChildren()) {
			lastCepRuleItem = cepRuleItem;
			normalizeCepRuleItem(cepRule, cepRuleItem);
		}
		if (lastCepRuleItem != null && StringUtils.isNotEmpty(group.getConditionGroup())) {
			lastCepRuleItem.setCondition(group.getConditionGroup());
		}
	}

	protected void normalizeCondition(final CepRule cepRule, CepRuleItem condition) {
		ToolField field = cache.get(condition.getField());
		if (field == null) {
			condition.setFieldType(FieldType.STRING.external);
		} else {
			condition.setFieldType(field.getType().external);
			if (FieldType.DOUBLE == field.getType()) {
				if (condition.getValueMin().indexOf('.') == -1) {
					condition.setValueMin(condition.getValueMin() + ".0");
				}
			}
		}
		if (StringUtils.isEmpty(condition.getCondition())) {
			condition.setCondition(Condition.AND.external);
		}
	}

	public CepRule save(final CepRule value) {
		Validate.notNull(value, "CepRule can not be null");
		Validate.notNull(value.getTool());
		Validate.notEmpty(value.getMessage());

		this.normalize(value);
		if (StringUtils.isEmpty(value.getCepRuleId())) {
			this.processCreate(value);
		} else {
			this.processUpdate(value);
		}
		value.setRemovedDate(null);
		value.setRemovedBy(null);
		value.setRemoved(Boolean.FALSE);
		value.setPlan(siddhiService.generateExecutionPlan(value));

		CepRule cepRule = cepRuleRepository.save(value);
		return cepRule;
	}

	protected void processCreate(final CepRule cepRule) {
		Validate.isTrue(StringUtils.isEmpty(cepRule.getCepRuleId()));

		final String user = userService.getAuthenticatedUserName();
		cepRule.setCepRuleId(String.valueOf(cepRuleRepository.count() + 1));
		cepRule.setCreatedDate(this.now());
		cepRule.setCreatedBy(user);

		cepRule.setChangedDate(null);
		cepRule.setChangedBy(null);

		this.populateSituation(cepRule);

	}

	protected void processUpdate(final CepRule cepRule) {
		Validate.notEmpty(cepRule.getCepRuleId());

		final String user = userService.getAuthenticatedUserName();

		CepRule cepRuleOld = cepRuleRepository.findOne(cepRule.getCepRuleId());
		checkPrivilegesToUpdate(user, cepRuleOld);

		cepRule.setCreatedDate(cepRuleOld.getCreatedDate());
		cepRule.setCreatedBy(cepRuleOld.getCreatedBy());
		cepRule.setSituation(cepRuleOld.getSituation());

		cepRule.setChangedDate(this.now());
		cepRule.setChangedBy(user);

	}

	public CepRule remove(final String id) {
		CepRule cepRule = this.findOne(id);
		return this.remove(cepRule);
	}

	public CepRule remove(final CepRule cepRule) {
		Validate.notNull(cepRule);
		Validate.notEmpty(cepRule.getCepRuleId());

		final String user = userService.getAuthenticatedUserName();

		CepRule cepRuleOld = cepRuleRepository.findOne(cepRule.getCepRuleId());

		checkPrivilegesToRemove(user, cepRuleOld);

		cepRule.setRemovedDate(this.now());
		cepRule.setRemovedBy(user);
		cepRule.setRemoved(Boolean.TRUE);

		cepRuleOld = cepRuleRepository.save(cepRuleOld);

		return cepRuleOld;

	}

	protected void checkPrivilegesToUpdate(final String user, CepRule cepRule) {
		Validate.isTrue(user.equals(cepRule.getCreatedBy()));
	}

	protected void checkPrivilegesToRemove(final String user, CepRule cepRule) {
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
		String situation = cepRule.getTool().getNickName();
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
		logger.info("findAll()");
		return cepRuleRepository.findAll();
	}

	public Iterable<CepRule> findAtivos() {
		logger.info("findAtivos()");
		return cepRuleRepository.findByRemoved(Boolean.FALSE);
	}

	public CepRule findOne(String id) {
		logger.info("findOne() " + id);
		CepRule cepRule = cepRuleRepository.findOne(id);
		return cepRule;
	}

	public List<CepRule> findBySituation(String situation) {
		logger.info("findBySituation() " + situation);
		List<CepRule> list = cepRuleRepository.findBySituationStartingWith(situation);
		return list;
	}

}
