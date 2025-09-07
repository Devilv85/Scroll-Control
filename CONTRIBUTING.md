# Contributing to ThinkPad Control 🤝

Thank you for your interest in contributing to ThinkPad Control! We welcome contributions from developers of all skill levels. This guide will help you get started.

## 🌟 Ways to Contribute

### 🐛 Bug Reports
- Report bugs through [GitHub Issues](../../issues)
- Include device model, Android version, and steps to reproduce
- Provide screenshots or screen recordings when helpful

### 💡 Feature Requests
- Suggest new features through [GitHub Issues](../../issues)
- Explain the problem you're trying to solve
- Describe your proposed solution

### 🔧 Code Contributions
- Fix bugs or implement new features
- Improve documentation
- Add tests
- Optimize performance

### 🌍 Translations
- Help translate the app to other languages
- Review existing translations for accuracy

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 11 or higher
- Git
- Basic knowledge of Kotlin and Android development

### Setting Up Development Environment

1. **Fork the repository**
   ```bash
   # Click the "Fork" button on GitHub, then clone your fork
   git clone https://github.com/yourusername/thinkpad-control.git
   cd thinkpad-control
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run tests**
   ```bash
   ./gradlew test
   ```

## 📋 Development Guidelines

### Code Style
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add comments for complex logic
- Keep functions small and focused

### Architecture
- Follow MVVM pattern
- Use dependency injection with Hilt
- Implement proper error handling
- Write unit tests for business logic

### Git Workflow
1. Create a feature branch from `main`
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. Make your changes
3. Write or update tests
4. Commit with descriptive messages
   ```bash
   git commit -m "Add: New feature description"
   ```

5. Push to your fork
   ```bash
   git push origin feature/your-feature-name
   ```

6. Create a Pull Request

### Commit Message Format
Use conventional commit format:
- `feat:` for new features
- `fix:` for bug fixes
- `docs:` for documentation changes
- `style:` for formatting changes
- `refactor:` for code refactoring
- `test:` for adding tests
- `chore:` for maintenance tasks

## 🧪 Testing

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# All tests
./gradlew check
```

### Writing Tests
- Write unit tests for ViewModels and repositories
- Add instrumented tests for UI components
- Test edge cases and error conditions
- Maintain good test coverage

## 📝 Pull Request Process

### Before Submitting
- [ ] Code builds without errors
- [ ] All tests pass
- [ ] Code follows project style guidelines
- [ ] Documentation is updated if needed
- [ ] Changes are tested on multiple Android versions

### Pull Request Template
When creating a PR, please include:

**Description**
- Brief description of changes
- Link to related issue (if applicable)

**Type of Change**
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Performance improvement
- [ ] Other (please describe)

**Testing**
- [ ] Unit tests added/updated
- [ ] Manual testing completed
- [ ] Tested on multiple devices/versions

**Screenshots**
- Include screenshots for UI changes

### Review Process
1. Automated checks must pass
2. Code review by maintainers
3. Address feedback if requested
4. Final approval and merge

## 🐛 Bug Fix Guidelines

### Finding Bugs
- Check existing issues first
- Reproduce the bug consistently
- Identify the root cause
- Write a test that fails before the fix

### Fixing Process
1. Create a bug report issue
2. Create a branch: `fix/issue-number-description`
3. Write a failing test
4. Implement the fix
5. Ensure the test passes
6. Submit a pull request

## ✨ Feature Development

### Planning
- Discuss major features in issues first
- Consider impact on existing functionality
- Plan for backward compatibility
- Design with accessibility in mind

### Implementation
- Break large features into smaller PRs
- Update documentation
- Add appropriate tests
- Consider performance implications

## 📖 Documentation

### Code Documentation
- Add KDoc comments for public APIs
- Explain complex algorithms
- Document assumptions and constraints
- Keep comments up to date

### User Documentation
- Update README.md for new features
- Add usage examples
- Update screenshots if UI changes
- Consider creating tutorials

## 🔒 Security Considerations

### Privacy
- Never collect unnecessary user data
- Encrypt sensitive information
- Follow Android security best practices
- Document privacy implications

### Code Security
- Validate all inputs
- Handle errors gracefully
- Avoid hardcoded secrets
- Use secure communication

## 🎯 Accessibility

### Guidelines
- Support screen readers
- Provide content descriptions
- Ensure sufficient color contrast
- Test with accessibility services enabled

### Testing
- Use TalkBack to test navigation
- Verify keyboard navigation works
- Test with large text sizes
- Check color contrast ratios

## 🌍 Internationalization

### Adding Translations
1. Create string resources in `res/values-{locale}/strings.xml`
2. Use string resources instead of hardcoded text
3. Consider text expansion in different languages
4. Test with RTL languages

### Guidelines
- Keep strings concise but clear
- Provide context for translators
- Avoid concatenating translated strings
- Use plurals appropriately

## 📞 Getting Help

### Communication Channels
- **GitHub Issues**: Bug reports and feature requests
- **GitHub Discussions**: General questions and ideas
- **Code Reviews**: Feedback on pull requests

### Questions?
- Check existing documentation first
- Search through issues and discussions
- Ask specific, detailed questions
- Provide context and examples

## 🏆 Recognition

Contributors will be recognized in:
- README.md acknowledgments
- Release notes
- GitHub contributors list

## 📄 License

By contributing to ThinkPad Control, you agree that your contributions will be licensed under the GPL-3.0 License.

---

Thank you for contributing to ThinkPad Control! Together, we can help people build healthier relationships with technology. 🚀