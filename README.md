# PHP Version Switcher Plugin

[![PhpStorm](http://img.shields.io/badge/-PHPStorm-000000?style=for-the-badge&logo=phpstorm&logoColor=white)](https://plugins.jetbrains.com/plugin/25086-php-version-switcher)
[![IDEA Ultimate](https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)](https://plugins.jetbrains.com/plugin/25086-php-version-switcher)
[![Version](https://img.shields.io/jetbrains/plugin/v/25086-php-version-switcher.svg?style=for-the-badge&label=version)](https://plugins.jetbrains.com/plugin/25086-php-version-switcher/versions)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/25086-php-version-switcher.svg?style=for-the-badge)](https://plugins.jetbrains.com/plugin/25086-php-version-switcher)
[![License](https://img.shields.io/badge/license-MIT-428F7E.svg?style=for-the-badge)](https://github.com/uuf6429/jetbrains-php-version-switcher/blob/main/LICENSE)

This is an IDE plugin that switches
the [PHP Language Level](https://www.jetbrains.com/help/phpstorm/supported-php-versions.html) automatically whenever an
editor window is focused, based on the version defined in the nearest `composer.json` file (to the focused editor file).

That behaviour would be helpful if your project contains PHP code intended to run on different PHP versions
(PhpStorm/IDEA) or if you are opening multiple projects as modules (IDEA).

https://github.com/user-attachments/assets/db36e668-5f40-4e8d-b69c-62a518c7223a

## _Caveat ingeniator_

### Directory-based Behaviour _(at the moment)_

You cannot have multiple PHP files with different language level in the same directory, since there can only be
one `composer.json` per directory.

### The `composer.json` Requirement

The PHP version is only switched when a `composer.json` file exists, containing a `"php"` entry in the `"require"`
section. The `"require-dev"` section is currently ignored.

### Conflicting Split Views

The language level is still a global setting. If you have a PHP-7.4 file open next to a PHP-5.6 file, and you focus the
PHP-5.6 one, errors/warnings will probably show up in the PHP-7.4 file since it tries to apply the PHP version there.
