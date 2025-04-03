import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { css, unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin';
import $cssFromFile_0 from 'Frontend/generated/jar-resources/styles/toolbar-button.css?inline';
const $css_0 = typeof $cssFromFile_0  === 'string' ? unsafeCSS($cssFromFile_0) : $cssFromFile_0;
registerStyles('vaadin-button', $css_0, {moduleId: 'flow_css_mod_0'});
import $cssFromFile_1 from 'print-js/dist/print.css?inline';

injectGlobalCss($cssFromFile_1.toString(), 'CSSImport end', document);
import $cssFromFile_2 from 'Frontend/generated/jar-resources/styles.css?inline';

injectGlobalCss($cssFromFile_2.toString(), 'CSSImport end', document);
import $cssFromFile_3 from 'Frontend/generated/jar-resources/ckeditor.css?inline';

injectGlobalCss($cssFromFile_3.toString(), 'CSSImport end', document);
import $cssFromFile_4 from 'Frontend/generated/jar-resources/styles/wizard.css?inline';

injectGlobalCss($cssFromFile_4.toString(), 'CSSImport end', document);
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/text-field/theme/lumo/vaadin-text-field.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/form-layout/theme/lumo/vaadin-form-layout.js';
import '@vaadin/dialog/theme/lumo/vaadin-dialog.js';
import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import '@vaadin/password-field/theme/lumo/vaadin-password-field.js';
import '@vaadin/email-field/theme/lumo/vaadin-email-field.js';
import '@vaadin/vertical-layout/theme/lumo/vaadin-vertical-layout.js';
import '@vaadin/tooltip/theme/lumo/vaadin-tooltip.js';
import '@vaadin/icon/theme/lumo/vaadin-icon.js';
import '@vaadin/form-layout/theme/lumo/vaadin-form-item.js';
import '@vaadin/horizontal-layout/theme/lumo/vaadin-horizontal-layout.js';
import '@vaadin/button/theme/lumo/vaadin-button.js';
import 'Frontend/generated/jar-resources/buttonFunctions.js';
import '@vaadin/notification/theme/lumo/vaadin-notification.js';
import '@vaadin/combo-box/theme/lumo/vaadin-combo-box.js';
import 'Frontend/generated/jar-resources/comboBoxConnector.js';
import 'Frontend/generated/jar-resources/vaadin-grid-flow-selection-column.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-column.js';
import '@vaadin/app-layout/theme/lumo/vaadin-app-layout.js';
import '@vaadin/side-nav/theme/lumo/vaadin-side-nav-item.js';
import 'Frontend/generated/jar-resources/dndConnector.js';
import '@vaadin/context-menu/theme/lumo/vaadin-context-menu.js';
import 'Frontend/generated/jar-resources/contextMenuConnector.js';
import 'Frontend/generated/jar-resources/contextMenuTargetConnector.js';
import '@vaadin/grid/theme/lumo/vaadin-grid.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-sorter.js';
import '@vaadin/checkbox/theme/lumo/vaadin-checkbox.js';
import 'Frontend/generated/jar-resources/gridConnector.js';
import 'Frontend/generated/jar-resources/menubarConnector.js';
import '@vaadin/menu-bar/theme/lumo/vaadin-menu-bar.js';
import '@vaadin/text-area/theme/lumo/vaadin-text-area.js';
import '@vaadin/app-layout/theme/lumo/vaadin-drawer-toggle.js';
import 'Frontend/generated/jar-resources/so/chart/chart.js';
import '@vaadin/avatar/theme/lumo/vaadin-avatar.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-column-group.js';
import 'Frontend/generated/jar-resources/lit-renderer.ts';
import '@vaadin/confirm-dialog/theme/lumo/vaadin-confirm-dialog.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '130fdac2f606b415395067da45456dd0d791445544f7ad3d166f02d18e3093de') {
    pending.push(import('./chunks/chunk-64c41df76cfe53fac04fa7679a4975a703727040f537f370e2b87bc1ed99c88a.js'));
  }
  if (key === 'ba0740ecd7eaf5a968f0c2ab0d8956e90792075a7e15f0bc1d6802044a5a4d45') {
    pending.push(import('./chunks/chunk-bbd0c32a867aabeb4ee623fbc01288773607f95df6ef6704d2a75e7e6dc2934d.js'));
  }
  if (key === 'dda84c20fd579ccd7d28114311436dc3550664fcd38333fffadcd8f2beff85e4') {
    pending.push(import('./chunks/chunk-86f0dd8e08708e9d65a88ac390e1370d4f15087dced3c7d8b4930f2074c3b981.js'));
  }
  if (key === '3368b28ae401a37aec04b5f8d138532a46b6c3b9c4d4377cb3615fb35b8d388d') {
    pending.push(import('./chunks/chunk-bbd0c32a867aabeb4ee623fbc01288773607f95df6ef6704d2a75e7e6dc2934d.js'));
  }
  if (key === '0153f681941e1b720af11bbb8ba0a99e916d03102d8fa24ec5b23c9dc7d7b74e') {
    pending.push(import('./chunks/chunk-03450d900a9b585f1f3e4f6fb8f8606001112537592c7e9213828f50c21035c2.js'));
  }
  if (key === '61de215dc74f10ce4838d50ec138c6796d05143f35a6234f87c9566338c90a2b') {
    pending.push(import('./chunks/chunk-03450d900a9b585f1f3e4f6fb8f8606001112537592c7e9213828f50c21035c2.js'));
  }
  if (key === '9dcdce97eb6be983b7fbbda11f5ead2fa8ba6e14013d655cfe2e7cad3335963c') {
    pending.push(import('./chunks/chunk-b45f1ea93f69918bbbd12dad38f606f5beab72ff32848f61eb9f6eafc375edaa.js'));
  }
  if (key === '49ed8ff4166585dcf29b567e05f6fe39fce200af1c1a8ae7b5ce3f0a204c39e0') {
    pending.push(import('./chunks/chunk-b5f9ea890fe41878022d53b89a4da96c7073d90a14035d2e7c53377bfcf84162.js'));
  }
  if (key === 'cc6a9a9a230166e119979efdc09d9ef284a90119880c86c8af928ccf83fc7b88') {
    pending.push(import('./chunks/chunk-64c41df76cfe53fac04fa7679a4975a703727040f537f370e2b87bc1ed99c88a.js'));
  }
  if (key === '8cd1296780278484c56befb9de72ec763042f3535ec9325e2a92b3a68c14746f') {
    pending.push(import('./chunks/chunk-86f0dd8e08708e9d65a88ac390e1370d4f15087dced3c7d8b4930f2074c3b981.js'));
  }
  if (key === 'ed28b5faf3885a5e351a83211b1dd8e139c98b9849f40792e127744d5d3bed2d') {
    pending.push(import('./chunks/chunk-85e08af653201055b013ba4a38acad49329ae9f401b784f3517ec74bd5fe9df8.js'));
  }
  if (key === '21d3b6ee93a374fd3dc19fd4d02e858024d453eed3ba592ab9abc8b257977d38') {
    pending.push(import('./chunks/chunk-03450d900a9b585f1f3e4f6fb8f8606001112537592c7e9213828f50c21035c2.js'));
  }
  if (key === '42bb99999e2c1e88421f77d963f4acff8c5e62364dff2127fc662ac9fea1cdfc') {
    pending.push(import('./chunks/chunk-388d39a710d98e6a74b937f8b06bda13f47af0e42cbbaad0de1f058fc23494d3.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;