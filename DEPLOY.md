# GitHub Actions CI/CD 설정 가이드

## 필요한 GitHub Secrets

GitHub 레포지토리 Settings > Secrets and variables > Actions 에서 다음 시크릿을 설정하세요.

### 백엔드 (Final-back)

| Secret Name | 설명 | 예시 |
|-------------|------|------|
| `EC2_HOST` | EC2 인스턴스 IP | `34.219.149.156` |
| `EC2_USER` | SSH 사용자 | `ubuntu` |
| `SSH_PRIVATE_KEY` | EC2 접속용 PEM 키 전체 내용 | `-----BEGIN RSA PRIVATE KEY-----...` |
| `DB_URL` | Oracle DB URL | `jdbc:oracle:thin:@host:port:SID` |
| `DB_USERNAME` | DB 사용자명 | `C##USER` |
| `DB_PASSWORD` | DB 비밀번호 | |
| `GOOGLE_CLIENT_ID` | Google OAuth Client ID | |
| `GOOGLE_CLIENT_SECRET` | Google OAuth Client Secret | |
| `JWT_SECRET` | JWT 시크릿 키 | |
| `AWS_ACCESS_KEY` | AWS Access Key | |
| `AWS_SECRET_KEY` | AWS Secret Key | |
| `AWS_REGION` | AWS 리전 | `us-west-2` |
| `AWS_S3_BUCKET` | S3 버킷 이름 | `final-replay` |
| `NAVER_CLIENT_ID` | 네이버 API Client ID | |
| `NAVER_CLIENT_SECRET` | 네이버 API Client Secret | |

## 사용 방법

1. `develop` 또는 `main` 브랜치에 push하면 자동 배포
2. GitHub Actions 탭에서 수동 실행도 가능 (workflow_dispatch)

## 배포 프로세스

1. Java 21 (Temurin) 설치
2. Gradle로 bootJar 빌드
3. application.yml, application-private.yml 생성
4. EC2에 SSH 접속
5. JAR 파일 업로드
6. 설정 파일 업로드
7. API 컨테이너 재시작
8. 배포 확인

## 주의사항

- 백엔드 시작에 약 30초 소요
- OAuth redirect-uri가 EC2 IP로 설정됨
- Google Cloud Console에서 해당 redirect URI 허용 필요
