package br.produban.bdm.ceprule.ws.rest;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import br.produban.bdm.ceprule.commons.Region;
import br.produban.bdm.ceprule.model.CepRule;

public class CepRuleGemfireServiceDeserializer extends JsonDeserializer<Region<?>> {

	protected CollectionType collectionType = TypeFactory.defaultInstance().constructCollectionType(List.class,
			CepRule.class);

	@Override
	public Region<CepRule> deserialize(JsonParser jp, DeserializationContext ctx)
			throws IOException, JsonProcessingException {

		jp.nextValue();

		Region<CepRule> result = new Region<CepRule>();
		result.setName(jp.getCurrentName());

		List<CepRule> list = jp.getCodec().readValue(jp, collectionType);
		result.setList(list);

		return result;
	}

}