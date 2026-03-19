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
import '@vaadin/tooltip/theme/lumo/vaadin-tooltip.js';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/text-field/theme/lumo/vaadin-text-field.js';
import '@vaadin/horizontal-layout/theme/lumo/vaadin-horizontal-layout.js';
import '@vaadin/button/theme/lumo/vaadin-button.js';
import 'Frontend/generated/jar-resources/buttonFunctions.js';
import '@vaadin/password-field/theme/lumo/vaadin-password-field.js';
import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import '@vaadin/combo-box/theme/lumo/vaadin-combo-box.js';
import 'Frontend/generated/jar-resources/comboBoxConnector.js';
import 'Frontend/generated/jar-resources/vaadin-grid-flow-selection-column.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-column.js';
import '@vaadin/email-field/theme/lumo/vaadin-email-field.js';
import '@vaadin/app-layout/theme/lumo/vaadin-app-layout.js';
import '@vaadin/tabs/theme/lumo/vaadin-tab.js';
import '@vaadin/icon/theme/lumo/vaadin-icon.js';
import 'Frontend/generated/jar-resources/dndConnector.js';
import '@vaadin/context-menu/theme/lumo/vaadin-context-menu.js';
import 'Frontend/generated/jar-resources/contextMenuConnector.js';
import 'Frontend/generated/jar-resources/contextMenuTargetConnector.js';
import '@vaadin/form-layout/theme/lumo/vaadin-form-item.js';
import '@vaadin/grid/theme/lumo/vaadin-grid.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-sorter.js';
import '@vaadin/checkbox/theme/lumo/vaadin-checkbox.js';
import 'Frontend/generated/jar-resources/gridConnector.js';
import 'Frontend/generated/jar-resources/menubarConnector.js';
import '@vaadin/menu-bar/theme/lumo/vaadin-menu-bar.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/form-layout/theme/lumo/vaadin-form-layout.js';
import '@vaadin/dialog/theme/lumo/vaadin-dialog.js';
import '@vaadin/text-area/theme/lumo/vaadin-text-area.js';
import '@vaadin/vertical-layout/theme/lumo/vaadin-vertical-layout.js';
import '@vaadin/app-layout/theme/lumo/vaadin-drawer-toggle.js';
import '@vaadin/tabs/theme/lumo/vaadin-tabs.js';
import 'Frontend/generated/jar-resources/so/chart/chart.js';
import '@vaadin/avatar/theme/lumo/vaadin-avatar.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-column-group.js';
import 'Frontend/generated/jar-resources/lit-renderer.ts';
import '@vaadin/confirm-dialog/theme/lumo/vaadin-confirm-dialog.js';
import '@vaadin/notification/theme/lumo/vaadin-notification.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '9dcdce97eb6be983b7fbbda11f5ead2fa8ba6e14013d655cfe2e7cad3335963c') {
    pending.push(import('./chunks/chunk-0518a4f30ef40afe592b362079ba2e8d8e4ecde173ccd8badf7f3aa5a462d3a5.js'));
  }
  if (key === 'e2b1528daa991e7a583f2dc018520463d01a61c0aaafc44a5bba519e516deefd') {
    pending.push(import('./chunks/chunk-8d57e024f75015c590476dd20547f9ea30e375428b2e62cb7588c0c2c76e78f2.js'));
  }
  if (key === 'a05d857fcdc5b081ad7d41b438db2a50fe225f697d2a6cc50c1b9cefbbc62a73') {
    pending.push(import('./chunks/chunk-8d57e024f75015c590476dd20547f9ea30e375428b2e62cb7588c0c2c76e78f2.js'));
  }
  if (key === '61de215dc74f10ce4838d50ec138c6796d05143f35a6234f87c9566338c90a2b') {
    pending.push(import('./chunks/chunk-e97bc72eeacbd65edd7bd1d6b9ba40c9bd821f8dcab70dad6946d3121f75bf32.js'));
  }
  if (key === '0153f681941e1b720af11bbb8ba0a99e916d03102d8fa24ec5b23c9dc7d7b74e') {
    pending.push(import('./chunks/chunk-e97bc72eeacbd65edd7bd1d6b9ba40c9bd821f8dcab70dad6946d3121f75bf32.js'));
  }
  if (key === 'ba0740ecd7eaf5a968f0c2ab0d8956e90792075a7e15f0bc1d6802044a5a4d45') {
    pending.push(import('./chunks/chunk-302cebea4e63e9e04e0f7b2c79512b0564e05f905d1bc5de61663c0218b1c9af.js'));
  }
  if (key === 'dda84c20fd579ccd7d28114311436dc3550664fcd38333fffadcd8f2beff85e4') {
    pending.push(import('./chunks/chunk-8d57e024f75015c590476dd20547f9ea30e375428b2e62cb7588c0c2c76e78f2.js'));
  }
  if (key === '49ed8ff4166585dcf29b567e05f6fe39fce200af1c1a8ae7b5ce3f0a204c39e0') {
    pending.push(import('./chunks/chunk-9e7e28adb4783178e4b06113d86332b77c5912b1cf28c3bb449e2abad4ebcd36.js'));
  }
  if (key === 'cc6a9a9a230166e119979efdc09d9ef284a90119880c86c8af928ccf83fc7b88') {
    pending.push(import('./chunks/chunk-2228d94810b597da8b37e45e36e8de276a6a2d328ccd4cbf3468557e66915c72.js'));
  }
  if (key === '42bb99999e2c1e88421f77d963f4acff8c5e62364dff2127fc662ac9fea1cdfc') {
    pending.push(import('./chunks/chunk-5d35a8debbf4fc7b61c921baccfc578df0fa356b600fc6926d3a85d0593bbce0.js'));
  }
  if (key === '8cd1296780278484c56befb9de72ec763042f3535ec9325e2a92b3a68c14746f') {
    pending.push(import('./chunks/chunk-8d57e024f75015c590476dd20547f9ea30e375428b2e62cb7588c0c2c76e78f2.js'));
  }
  if (key === '21d3b6ee93a374fd3dc19fd4d02e858024d453eed3ba592ab9abc8b257977d38') {
    pending.push(import('./chunks/chunk-e97bc72eeacbd65edd7bd1d6b9ba40c9bd821f8dcab70dad6946d3121f75bf32.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;