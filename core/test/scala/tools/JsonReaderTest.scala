package tools

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.{Assertions, Test}
class JsonReaderTest {
    @DisplayName("test_read_json_from_path")
    @Test
    def test_read_json_from_path(): Unit = {
        var json_result=JsonReader.read_json_from_path("test_json.json")
        assert(json_result.getJSONObject("grandfather").getString("body_type").equals("dynamic"),"grandfather is dynamic")
        assert(!json_result.getJSONObject("daughter").has("shape"), "daughter not extends yet")

    }
}
