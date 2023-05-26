
# PostMedium: A Tool for Posting Markdown Documents to Medium via API

PostMedium is a command-line Java application that uses the Medium API to publish markdown documents as Medium blog posts. This README guides you on how to use PostMedium.

## Prerequisites

1. Java Development Kit (JDK) 17 or higher. You can download it [here](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).
2. An IDE such as IntelliJ IDEA or Eclipse, or a simple text editor if you're comfortable working from the command line.
3. A Medium account with a valid Integration Token. You can get this by [Creating a new integration](https://medium.com/me/settings/security) within your Medium settings. See integration tokens.
4. Your Medium author ID. This is the unique identifier associated with your Medium account.

## Setup

1. Clone the PostMedium repository from GitHub. [post medium](https://github.com/extremelogic-ph/post_medium)
2. Open the project in your preferred IDE.
3. Update `application.yaml` file located in `./resources` directory with your Medium account's `integration-token`, `author-id`, and other necessary details. The file should look like this:

```yaml
medium:
  account:
    integration-token: <Your Medium integration token here>
    author-id: <Your Medium author id here>
  content-format: markdown
  tags: <Comma separated tags for your post>
  publish-status: public
  canonical-url: <Canonical URL of the post>
  file: <Markdown file name>
  file-path: <Path where the markdown file is located>
  api-base-url: https://api.medium.com/v1/users/${medium.account.author-id}/posts

```
4. Save your markdown document in the directory specified in `file-path`. Ensure the name of the markdown file matches the name provided in `file`.

## How to Run

From the root directory of the project, execute the following command:

```bash
./mvnw spring-boot:run
```

This will start the PostMedium application. It will read the markdown file specified in the `application.yaml` file and post it as a new blog on Medium under your account. The post's title will be taken from the first line of your markdown file.

If everything is set up correctly, you should see logs indicating the title of your post and the response from the Medium API in the console.

## Troubleshooting

- Ensure you have a valid Medium integration token and author ID set up in your `application.yaml` file.
- Ensure your markdown file is saved in the correct location and the `file` and `file-path` properties in the `application.yaml` file are correctly set.
- If you encounter any errors during execution, check the error message displayed in the logs for clues about what went wrong.

## Limitations

- Currently, the application only supports markdown content. HTML and other formats are not supported.
- The Medium API has rate limits. If you encounter any rate limit errors, wait for some time before attempting to post again.
- The Medium API does not support updating already published posts. This application is intended to create new posts only.

## Contribution

Feel free to fork this project, raise issues and submit Pull Requests.

## License

This project is open source and available under the MIT License.

If you have any further questions, you can contact me via my email.