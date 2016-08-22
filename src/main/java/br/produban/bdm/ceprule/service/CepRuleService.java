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

import br.produban.bdm.ceprule.commons.ExtendableBean;
import br.produban.bdm.ceprule.model.CepRule;
import br.produban.bdm.ceprule.model.CepRuleItem;
import br.produban.bdm.ceprule.model.ToolField;
import br.produban.bdm.ceprule.ws.rest.CepRuleGemfireServiceClient;

/**
 * Created by pedrozatta
 */

@Service
public class CepRuleService {

	final static Logger logger = Logger.getLogger(CepRuleService.class);

	@Autowired
	private SiddhiService siddhiService;

	@Autowired
	private CepRuleGemfireServiceClient cepRuleRepository;

	@Autowired
	private UserService userService;


	public CepRule save(final CepRule value) {
		Validate.notNull(value, "CepRule can not be null");
		Validate.notNull(value.getTool());
		Validate.notEmpty(value.getMessage());

		// this.normalize(value);
		if (StringUtils.isEmpty(value.getCepRuleId())) {
			this.processCreate(value);
		} else {
			this.processUpdate(value);
		}
		value.setRemovedDate(null);
		value.setRemovedBy(null);
		value.setRemoved(Boolean.FALSE);
		value.setPlan(siddhiService.generateExecutionPlan(value));

		cepRuleRepository.save(value);
		return value;
	}

	protected void processCreate(final CepRule cepRule) {
		Validate.isTrue(StringUtils.isEmpty(cepRule.getCepRuleId()));

		final String user = userService.getAuthenticatedUserName();
		// cepRule.setCepRuleId(String.valueOf(cepRuleRepository.count() + 1));
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

		cepRuleOld.setRemovedDate(this.now());
		cepRuleOld.setRemovedBy(user);
		cepRuleOld.setRemoved(Boolean.TRUE);

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

		try {
			int i = ++x;
			cepRule.setSituation(situationPrefix + "_" + i);
		} catch (java.lang.NumberFormatException e) {
			cepRule.setSituation(situationPrefix + "_1");
		}
	}

	protected String generateSituationPrefix(CepRule cepRule) {
		String situation = cepRule.getTool().getNickName();
		CepRuleItem item = null;
		try {
			item = cepRule.getField(ToolField.FIELD_METRIC);
		} catch (NullPointerException e) {
			item = cepRule.getField(ToolField.FIELD_METRIC_NAME);
		}
		if (item != null) {
			situation += "_metric_" + item.getValueMin();
		}
		situation = situation.replaceAll("[^a-zA-Z1-9_]", "_");
		return situation;
	}

	protected Date now() {
		return Calendar.getInstance().getTime();
	}

	public Iterable<CepRule> findAll() {
		logger.info("findAll()");
		return cepRuleRepository.findAll();
	}

	public ExtendableBean getHighlights() {
		logger.info("getHighlights(..) ");
		long rules = 0;
		long trash = 0;

		long myRules = 0;
		long myTrash = 0;

		final String user = userService.getAuthenticatedUserName();

		Iterable<CepRule> list = cepRuleRepository.findAll();
		for (CepRule item : list) {
			rules++;
			if (item.getRemoved()) {
				trash++;
			}
			if (user.equalsIgnoreCase(item.getCreatedBy())) {
				myRules++;
				if (item.getRemoved()) {
					myTrash++;
				}
			}
		}

		ExtendableBean value = new ExtendableBean();
		value.add("rules", rules);
		value.add("trash", trash);
		value.add("active", rules);
		value.add("myRules", myRules);
		value.add("myTrash", myTrash);
		value.add("myActive", myRules);
		return value;
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
