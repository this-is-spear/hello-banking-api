package numble.bankingapi;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(controllers = HelloController.class)
@WithMockUser
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class HelloDocumentTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  void addExtension() throws Exception {
    MockHttpServletRequestBuilder builder = RestDocumentationRequestBuilders
        .get("/hello");

    mockMvc.perform(builder)
        .andExpect(status().isOk())
        .andDo(
            document(
                "hello",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters()
            )
        );
  }

  private OperationRequestPreprocessor getDocumentRequest() {
    return Preprocessors.preprocessRequest(
        Preprocessors.modifyUris()
            .scheme("http")
            .host("127.0.0.1")
            .port(8080),
        Preprocessors.prettyPrint()
    );
  }

  private OperationResponsePreprocessor getDocumentResponse() {
    return Preprocessors.preprocessResponse(Preprocessors.prettyPrint());
  }
}
