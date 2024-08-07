# PHP Version Switcher Plugin for PhpStorm/IDEA

This is a plugin that switches
the [PHP Language Level](https://www.jetbrains.com/help/phpstorm/supported-php-versions.html) automatically whenever an
editor window is focused, based on the version defined in the nearest `composer.json` file (to the focused editor file).

That behaviour would be helpful if your project contains PHP code intended to run on different PHP versions
(PhpStorm/IDEA) or if you are opening multiple projects as modules (IDEA).

## Caveats

### Directory-based Behaviour _(at the moment)_

You cannot have multiple PHP files with different language level in the same directory, since there can only be
one `composer.json` per directory.

### The `composer.json` Requirement

The PHP version is only switched when a `composer.json` file exists, containing a "php" entry in the "require" section.
The "require-dev" section is currently ignored.

### Conflicting Split Views

The language level is still a global setting. If you have a PHP-7.4 file open next to a PHP-5.6 file, and you focus the
PHP-5.6 one, errors/warnings will probably show up in the PHP-7.4 file since it tries to apply the PHP version there.

## Next Steps

- project-level setting to disable the plugin
- considering retrieving the language level from `require-dev` and/or `composer.lock`
- consider retrieving the language level from the file itself via special-crafted (phpdoc)comments
