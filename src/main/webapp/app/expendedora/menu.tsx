import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const ExpendedoraMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/expendedora">
        Maquina Expendedora
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default ExpendedoraMenu;
