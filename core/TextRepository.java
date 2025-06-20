package core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 텍스트 파일에서 연습용 문장 목록을 불러오는 저장소 클래스
 * 
 * 한국어와 영어 텍스트 파일을 읽어와서 타이핑 연습용 문장 리스트를 
 * 생성하고 관리합니다. 파일 읽기 오류 발생 시 기본 문장을 제공합니다.
 * 
 * @author JAVA 중간 프로젝트
 * @version 1.0
 */
public class TextRepository {

    /**
     * 지정된 파일 경로에서 텍스트를 읽어와 문장 목록을 반환합니다.
     * 클래스패스에서 리소스를 로드합니다.
     * 
     * @param resourcePath 읽어올 텍스트 파일의 클래스패스 경로
     * @return 연습용 문장들의 리스트
     */
    public List<String> loadTexts(String resourcePath) {
        List<String> texts = new ArrayList<>();
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            
            if (is == null) {
                System.err.println("리소스를 찾을 수 없습니다: " + resourcePath);
                texts.add("오류: 리소스 파일을 찾을 수 없습니다.");
                return texts;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    texts.add(line.trim());
                }
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("리소스 로딩 중 오류 발생: " + resourcePath + " - " + e.getMessage());
            texts.add("오류: 파일을 읽는 중 문제가 발생했습니다.");
        }
        
        if (texts.isEmpty()) {
            texts.add("기본 예제 텍스트입니다. 파일 내용을 확인해주세요.");
        }
        
        return texts;
    }

    /**
     * 지정된 파일 경로에서 전체 텍스트를 하나의 문자열로 읽어와 반환합니다.
     * 클래스패스에서 리소스를 로드합니다.
     * 
     * @param resourcePath 읽어올 텍스트 파일의 클래스패스 경로
     * @return 파일 전체 내용 문자열, 실패 시 에러 메시지
     */
    public String loadFullText(String resourcePath) {
        StringBuilder contentBuilder = new StringBuilder();
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            if (is == null) {
                System.err.println("리소스를 찾을 수 없습니다: " + resourcePath);
                return "오류: 리소스 파일을 찾을 수 없습니다: " + resourcePath;
            }
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (contentBuilder.length() > 0) {
                    contentBuilder.append(" ");
                }
                contentBuilder.append(line);
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("전체 텍스트 로딩 중 오류 발생: " + resourcePath + " - " + e.getMessage());
            return "파일을 불러오는 데 실패했습니다: " + resourcePath;
        }
        return contentBuilder.toString();
    }
} 