export interface IProducto {
  id?: number;
  clave?: number | null;
  nombreProducto?: string | null;
  precio?: number | null;
}

export const defaultValue: Readonly<IProducto> = {};
