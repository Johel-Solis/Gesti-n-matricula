package unicauca.edu.co.ms_gestion_maticula.infrastructure.utils;

import lombok.Data;

@Data
public class ApiResponse {
    private String typeResponse;
    private String message;
    private Object data;
    private int statusCode;

    public ApiResponse(String typeResponse, String message, Object data, int statusCode) {
        this.typeResponse = typeResponse;
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
    }

}
