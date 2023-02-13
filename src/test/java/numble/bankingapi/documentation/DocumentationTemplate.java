package numble.bankingapi.documentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@WithMockUser
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class DocumentationTemplate {

	@Autowired
	protected MockMvc mockMvc;


	protected OperationRequestPreprocessor getDocumentRequest() {
		return Preprocessors.preprocessRequest(
			Preprocessors.modifyUris()
				.scheme("http")
				.host("127.0.0.1")
				.port(8080),
			Preprocessors.prettyPrint()
		);
	}

	protected OperationResponsePreprocessor getDocumentResponse() {
		return Preprocessors.preprocessResponse(Preprocessors.prettyPrint());
	}
}
