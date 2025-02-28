export interface IProductoPagado {
  cantidadPagada?: number | null;
  cambio?: number | null;
  clave?: string | null;
}

export const defaultValue: Readonly<IProductoPagado> = {};
